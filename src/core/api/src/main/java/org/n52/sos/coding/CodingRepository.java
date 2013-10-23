/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.coding;

import static org.n52.sos.util.MultiMaps.newSetMultiMap;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.encode.ProcedureEncoder;
import org.n52.sos.encode.ResponseFormatKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.Activatable;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.CompositeSimilar;
import org.n52.sos.util.ProxySimilarityComparator;
import org.n52.sos.util.SetMultiMap;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CodingRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CodingRepository.class);

    private static CodingRepository instance;

    @SuppressWarnings("rawtypes")
    private final ServiceLoader<Decoder> serviceLoaderDecoder;

    @SuppressWarnings("rawtypes")
    private final ServiceLoader<Encoder> serviceLoaderEncoder;

    private final Set<Decoder<?, ?>> decoders;

    private final Set<Encoder<?, ?>> encoders;

    private final SetMultiMap<DecoderKey, Decoder<?, ?>> decoderByKey = newSetMultiMap();

    private final SetMultiMap<EncoderKey, Encoder<?, ?>> encoderByKey = newSetMultiMap();

    private SetMultiMap<SupportedTypeKey, Activatable<String>> typeMap = newSetMultiMap(SupportedTypeKey.class);

    private final Set<ObservationEncoder<?, ?>> observationEncoders = Sets.newHashSet();

    private final Map<String, Map<String, Set<String>>> responseFormats = Maps.newHashMap();

    private final Map<ResponseFormatKey, Boolean> responseFormatStatus = Maps.newHashMap();

    private final Map<String, Set<SchemaLocation>> schemaLocations = Maps.newHashMap();

    private final Map<String, Map<String, Set<String>>> procedureDescriptionFormats = Maps.newHashMap();

    private final Map<ProcedureDescriptionFormatKey, Boolean> procedureDescriptionFormatsStatus = Maps.newHashMap();

    /**
     * @return Returns a singleton instance of the CodingRepository.
     */
    public static CodingRepository getInstance() {
        if (instance == null) {
            instance = new CodingRepository();
        }
        return instance;
    }

    /**
     * private constructor for singleton
     */
    private CodingRepository() {
        this.serviceLoaderDecoder = ServiceLoader.load(Decoder.class);
        this.serviceLoaderEncoder = ServiceLoader.load(Encoder.class);
        this.decoders = Sets.newHashSet(loadDecoders());
        this.encoders = Sets.newHashSet(loadEncoders());
        initDecoderMap();
        initEncoderMap();
        generateTypeMap();
        generateResponseFormatMaps();
        generateProcedureDesccriptionFormatMaps();
        generateSchemaLocationMap();
    }

    @SuppressWarnings("unchecked")
    private <T> T unsafeCast(Object o) {
        return (T) o;
    }

    private <F, T> Decoder<F, T> processDecoderMatches(Set<Decoder<?, ?>> matches, DecoderKey key) {
        if (matches == null || matches.isEmpty()) {
            LOG.debug("No Decoder implementation for {}", key);
            return null;
        } else if (matches.size() > 1) {
            Decoder<?, ?> dec = Collections.min(matches, new DecoderComparator(key));
            LOG.warn("Requested ambiguous Decoder implementations for {}: Found {}; Choosing {}.", key, Joiner
                    .on(", ").join(matches), dec);
            return unsafeCast(dec);
        } else {
            return unsafeCast(matches.iterator().next());
        }
    }

    private <F, T> Encoder<F, T> processEncoderMatches(Set<Encoder<?, ?>> matches, EncoderKey key) {
        if (matches == null || matches.isEmpty()) {
            LOG.debug("No Encoder for {}", key);
            return null;
        } else if (matches.size() > 1) {
            Encoder<?, ?> enc = Collections.min(matches, new EncoderComparator(key));
            LOG.warn("Requested ambiguous Encoder implementations for {}: Found {}; Choosing {}.", key, Joiner
                    .on(", ").join(matches), enc);
            return unsafeCast(enc);
        } else {
            return unsafeCast(matches.iterator().next());
        }
    }

    public void updateDecoders() {
        LOG.debug("Reloading Decoder implementations");
        this.decoders.clear();
        this.decoders.addAll(loadDecoders());
        initDecoderMap();
        generateTypeMap();
        LOG.debug("Reloaded Decoder implementations");
    }

    public void updateEncoders() {
        LOG.debug("Reloading Encoder implementations");
        this.encoders.clear();
        this.encoders.addAll(loadEncoders());
        initEncoderMap();
        generateTypeMap();
        generateResponseFormatMaps();
        generateProcedureDesccriptionFormatMaps();
        generateSchemaLocationMap();
        LOG.debug("Reloaded Encoder implementations");
    }

    private void generateResponseFormatMaps() {
        this.responseFormatStatus.clear();
        this.responseFormats.clear();
        final Set<ServiceOperatorKey> serviceOperatorKeyTypes =
                ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes();
        for (Encoder<?, ?> e : getEncoders()) {
            if (e instanceof ObservationEncoder) {
                final ObservationEncoder<?, ?> oe = (ObservationEncoder<?, ?>) e;
                for (ServiceOperatorKey sokt : serviceOperatorKeyTypes) {
                    Set<String> rfs = oe.getSupportedResponseFormats(sokt.getService(), sokt.getVersion());
                    if (rfs != null) {
                        for (String rf : rfs) {
                            addResponseFormat(new ResponseFormatKey(sokt, rf));
                        }
                    }
                }
            }
        }
    }

    private void generateProcedureDesccriptionFormatMaps() {
        this.procedureDescriptionFormatsStatus.clear();
        this.procedureDescriptionFormats.clear();
        final Set<ServiceOperatorKey> serviceOperatorKeyTypes =
                ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes();
        for (Encoder<?, ?> e : getEncoders()) {
            if (e instanceof ProcedureEncoder) {
                final ProcedureEncoder<?, ?> oe = (ProcedureEncoder<?, ?>) e;
                for (ServiceOperatorKey sokt : serviceOperatorKeyTypes) {
                    Set<String> rfs = oe.getSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
                    if (rfs != null) {
                        for (String rf : rfs) {
                            addProcedureDescriptionFormat(new ProcedureDescriptionFormatKey(sokt, rf));
                        }
                    }
                }
            }
        }
    }

    private void generateSchemaLocationMap() {
        schemaLocations.clear();
        for (Encoder<?, ?> encoder : this.encoders) {
            if (CollectionHelper.isNotEmpty(encoder.getEncoderKeyType())) {
                for (EncoderKey key : encoder.getEncoderKeyType()) {
                    if (key instanceof XmlEncoderKey && CollectionHelper.isNotEmpty(encoder.getSchemaLocations())) {
                        schemaLocations.put(((XmlEncoderKey) key).getNamespace(), encoder.getSchemaLocations());
                    }
                }

            }
        }
    }

    protected void addResponseFormat(ResponseFormatKey rfkt) {
        try {
            this.responseFormatStatus.put(rfkt, SettingsManager.getInstance().isActive(rfkt));
        } catch (ConnectionProviderException ex) {
            throw new ConfigurationException(ex);
        }
        Map<String, Set<String>> byService = this.responseFormats.get(rfkt.getService());
        if (byService == null) {
            byService = Maps.newHashMap();
            this.responseFormats.put(rfkt.getService(), byService);
        }
        Set<String> byVersion = byService.get(rfkt.getVersion());
        if (byVersion == null) {
            byVersion = Sets.newHashSet();
            byService.put(rfkt.getVersion(), byVersion);
        }
        byVersion.add(rfkt.getResponseFormat());
    }

    protected void addProcedureDescriptionFormat(ProcedureDescriptionFormatKey pdfkt) {
        try {
            this.procedureDescriptionFormatsStatus.put(pdfkt, SettingsManager.getInstance().isActive(pdfkt));
        } catch (ConnectionProviderException ex) {
            throw new ConfigurationException(ex);
        }
        Map<String, Set<String>> byService = this.procedureDescriptionFormats.get(pdfkt.getService());
        if (byService == null) {
            byService = Maps.newHashMap();
            this.procedureDescriptionFormats.put(pdfkt.getService(), byService);
        }
        Set<String> byVersion = byService.get(pdfkt.getVersion());
        if (byVersion == null) {
            byVersion = Sets.newHashSet();
            byService.put(pdfkt.getVersion(), byVersion);
        }
        byVersion.add(pdfkt.getProcedureDescriptionFormat());
    }

    private List<Decoder<?, ?>> loadDecoders() {
        List<Decoder<?, ?>> loadedDecoders = new LinkedList<Decoder<?, ?>>();
        try {
            SettingsManager sm = SettingsManager.getInstance();
            for (Decoder<?, ?> decoder : serviceLoaderDecoder) {
                sm.configure(decoder);
                loadedDecoders.add(decoder);
            }
        } catch (ServiceConfigurationError sce) {
            String text = "An Decoder implementation could not be loaded!";
            LOG.warn(text, sce);
            throw new ConfigurationException(text, sce);
        }
        return loadedDecoders;
    }

    private List<Encoder<?, ?>> loadEncoders() {
        List<Encoder<?, ?>> loadedEncoders = new LinkedList<Encoder<?, ?>>();
        try {
            SettingsManager sm = SettingsManager.getInstance();
            for (Encoder<?, ?> encoder : serviceLoaderEncoder) {
                sm.configure(encoder);
                loadedEncoders.add(encoder);
            }
        } catch (ServiceConfigurationError sce) {
            String text = "An Encoder implementation could not be loaded!";
            LOG.warn(text, sce);
            throw new ConfigurationException(text, sce);
        }
        return loadedEncoders;
    }

    public Set<Decoder<?, ?>> getDecoders() {
        return CollectionHelper.unmodifiableSet(decoders);
    }

    public Set<Encoder<?, ?>> getEncoders() {
        return CollectionHelper.unmodifiableSet(encoders);
    }

    public Map<DecoderKey, Set<Decoder<?, ?>>> getDecoderByKey() {
        return CollectionHelper.unmodifiableMap(decoderByKey);
    }

    public Map<EncoderKey, Set<Encoder<?, ?>>> getEncoderByKey() {
        return CollectionHelper.unmodifiableMap(encoderByKey);
    }

    public Set<String> getFeatureOfInterestTypes() {
        return typesFor(SupportedTypeKey.FeatureType);
    }

    public Set<String> getObservationTypes() {
        return typesFor(SupportedTypeKey.ObservationType);
    }

    private Set<String> typesFor(SupportedTypeKey key) {
        if (typeMap == null || !typeMap.containsKey(key) || typeMap.get(key) == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(Activatable.filter(typeMap.get(key)));
    }

    private void generateTypeMap() {
        List<Map<SupportedTypeKey, Set<String>>> list = new LinkedList<Map<SupportedTypeKey, Set<String>>>();
        for (Decoder<?, ?> decoder : getDecoders()) {
            list.add(decoder.getSupportedTypes());
        }
        for (Encoder<?, ?> encoder : getEncoders()) {
            list.add(encoder.getSupportedTypes());
        }

        SetMultiMap<SupportedTypeKey, Activatable<String>> resultMap = newSetMultiMap(SupportedTypeKey.class);
        for (Map<SupportedTypeKey, Set<String>> map : list) {
            if (map != null && !map.isEmpty()) {
                for (SupportedTypeKey type : map.keySet()) {
                    if (map.get(type) != null && !map.get(type).isEmpty()) {
                        resultMap.addAll(type, Activatable.from(map.get(type)));
                    }
                }
            }
        }

        this.typeMap = resultMap;
    }

    private void initEncoderMap() {
        this.encoderByKey.clear();
        for (Encoder<?, ?> encoder : getEncoders()) {
            for (EncoderKey key : encoder.getEncoderKeyType()) {
                encoderByKey.add(key, encoder);
            }
            if (encoder instanceof ObservationEncoder) {
                observationEncoders.add((ObservationEncoder<?, ?>) encoder);
            }
        }
    }

    private void initDecoderMap() {
        this.decoderByKey.clear();
        for (Decoder<?, ?> decoder : getDecoders()) {
            for (DecoderKey key : decoder.getDecoderKeyTypes()) {
                decoderByKey.add(key, decoder);
            }
        }
    }

    public boolean hasDecoder(DecoderKey key, DecoderKey... keys) {
        return getDecoder(key, keys) != null;
    }

    public <F, T> Decoder<F, T> getDecoder(DecoderKey key, DecoderKey... keys) {
        if (keys.length == 0) {
            return getDecoderSingleKey(key);
        } else {
            return getDecoderCompositeKey(new CompositeDecoderKey(ImmutableList.<DecoderKey> builder().add(key)
                    .add(keys).build()));
        }
    }

    public boolean hasEncoder(EncoderKey key, EncoderKey... keys) {
        return getEncoder(key, keys) != null;
    }

    public <F, T> Encoder<F, T> getEncoder(EncoderKey key, EncoderKey... keys) {
        if (keys.length == 0) {
            return getEncoderSingleKey(key);
        } else {
            return getEncoderCompositeKey(new CompositeEncoderKey(ImmutableList.<EncoderKey> builder().add(key)
                    .add(keys).build()));
        }
    }

    public Set<SchemaLocation> getSchemaLocation(String namespace) {
        if (schemaLocations.containsKey(namespace)) {
            return schemaLocations.get(namespace);
        }
        return Sets.newHashSet();
    }

    private <F, T> Decoder<F, T> getDecoderSingleKey(DecoderKey key) {
        return processDecoderMatches(findDecodersForSingleKey(key), key);
    }

    private <F, T> Decoder<F, T> getDecoderCompositeKey(CompositeDecoderKey key) {
        return processDecoderMatches(findDecodersForCompositeKey(key), key);
    }

    private <F, T> Encoder<F, T> getEncoderSingleKey(EncoderKey key) {
        return processEncoderMatches(findEncodersForSingleKey(key), key);
    }

    private <F, T> Encoder<F, T> getEncoderCompositeKey(CompositeEncoderKey key) {
        return processEncoderMatches(findEncodersForCompositeKey(key), key);
    }

    private Set<Encoder<?, ?>> findEncodersForSingleKey(EncoderKey key) {
        if (!encoderByKey.containsKey(key)) {
            for (Encoder<?, ?> encoder : getEncoders()) {
                for (EncoderKey ek : encoder.getEncoderKeyType()) {
                    if (ek.getSimilarity(key) >= 0) {
                        encoderByKey.add(key, encoder);
                    }
                }
            }
        }
        return encoderByKey.get(key);
    }

    private Set<Decoder<?, ?>> findDecodersForSingleKey(DecoderKey key) {
        if (!decoderByKey.containsKey(key)) {
            for (Decoder<?, ?> decoder : getDecoders()) {
                for (DecoderKey dk : decoder.getDecoderKeyTypes()) {
                    if (dk.getSimilarity(key) >= 0) {
                        decoderByKey.add(key, decoder);
                    }
                }
            }
        }
        return decoderByKey.get(key);
    }

    private Set<Encoder<?, ?>> findEncodersForCompositeKey(CompositeEncoderKey ck) {
        if (!encoderByKey.containsKey(ck)) {
            // first request; search for matching encoders and save result for
            // later quries
            for (Encoder<?, ?> encoder : encoders) {
                if (ck.matches(encoder.getEncoderKeyType())) {
                    encoderByKey.add(ck, encoder);
                }
            }
            LOG.debug("Found {} Encoders for CompositeKey: {}", encoderByKey.get(ck).size(),
                    Joiner.on(", ").join(encoderByKey.get(ck)));
        }
        return encoderByKey.get(ck);
    }

    private Set<Decoder<?, ?>> findDecodersForCompositeKey(CompositeDecoderKey ck) {
        if (!decoderByKey.containsKey(ck)) {
            // first request; search for matching decoders and save result for
            // later queries
            for (Decoder<?, ?> decoder : decoders) {
                if (ck.matches(decoder.getDecoderKeyTypes())) {
                    decoderByKey.add(ck, decoder);
                }
            }
            LOG.debug("Found {} Decoders for CompositeKey: {}", decoderByKey.get(ck).size(),
                    Joiner.on(", ").join(decoderByKey.get(ck)));
        }
        return decoderByKey.get(ck);
    }

    public Map<ServiceOperatorKey, Set<String>> getSupportedResponseFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getSupportedResponseFormats(sokt));
        }
        return map;
    }

    public Set<String> getSupportedResponseFormats(ServiceOperatorKey sokt) {
        return getSupportedResponseFormats(sokt.getService(), sokt.getVersion());
    }

    public Set<String> getSupportedResponseFormats(String service, String version) {
        Map<String, Set<String>> byService = this.responseFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }

        ServiceOperatorKey sokt = new ServiceOperatorKey(service, version);
        Set<String> result = Sets.newHashSet();
        for (String a : rfs) {
            ResponseFormatKey rfkt = new ResponseFormatKey(sokt, a);
            final Boolean status = responseFormatStatus.get(rfkt);
            if (status != null && status.booleanValue()) {
                result.add(a);
            }
        }
        return result;
    }

    public Set<String> getAllSupportedResponseFormats(String service, String version) {
        Map<String, Set<String>> byService = this.responseFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(rfs);
    }

    public Map<ServiceOperatorKey, Set<String>> getAllSupportedResponseFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getAllSupportedResponseFormats(sokt));
        }
        return map;
    }

    public Set<String> getAllSupportedResponseFormats(ServiceOperatorKey sokt) {
        return getAllSupportedResponseFormats(sokt.getService(), sokt.getVersion());
    }

    public Map<ServiceOperatorKey, Set<String>> getSupportedProcedureDescriptionFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getSupportedProcedureDescriptionFormats(sokt));
        }
        return map;
    }

    public Set<String> getSupportedProcedureDescriptionFormats(ServiceOperatorKey sokt) {
        return getSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
    }

    public Set<String> getSupportedProcedureDescriptionFormats(String service, String version) {
        Map<String, Set<String>> byService = this.procedureDescriptionFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }

        ServiceOperatorKey sokt = new ServiceOperatorKey(service, version);
        Set<String> result = Sets.newHashSet();
        for (String a : rfs) {
            ProcedureDescriptionFormatKey pdfkt = new ProcedureDescriptionFormatKey(sokt, a);
            final Boolean status = procedureDescriptionFormatsStatus.get(pdfkt);
            if (status != null && status.booleanValue()) {
                result.add(a);
            }
        }
        return result;
    }

    public Map<ServiceOperatorKey, Set<String>> getAllProcedureDescriptionFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getAllSupportedProcedureDescriptionFormats(sokt));
        }
        return map;
    }

    public Set<String> getAllSupportedProcedureDescriptionFormats(String service, String version) {
        Map<String, Set<String>> byService = this.procedureDescriptionFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(rfs);
    }

    public Set<String> getAllSupportedProcedureDescriptionFormats(ServiceOperatorKey sokt) {
        return getAllSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
    }

    public void setActive(ResponseFormatKey rfkt, boolean active) {
        if (this.responseFormatStatus.containsKey(rfkt)) {
            this.responseFormatStatus.put(rfkt, active);
        }
    }

    public void setActive(ProcedureDescriptionFormatKey pdfk, boolean active) {
        if (this.procedureDescriptionFormatsStatus.containsKey(pdfk)) {
            this.procedureDescriptionFormatsStatus.put(pdfk, active);
        }
    }

    private class DecoderComparator extends ProxySimilarityComparator<Decoder<?, ?>, DecoderKey> {
        DecoderComparator(DecoderKey key) {
            super(key);
        }

        @Override
        protected Collection<DecoderKey> getSimilars(Decoder<?, ?> t) {
            return t.getDecoderKeyTypes();
        }
    }

    private class EncoderComparator extends ProxySimilarityComparator<Encoder<?, ?>, EncoderKey> {
        EncoderComparator(EncoderKey key) {
            super(key);
        }

        @Override
        protected Collection<EncoderKey> getSimilars(Encoder<?, ?> t) {
            return t.getEncoderKeyType();
        }
    }

    private class CompositeEncoderKey extends CompositeSimilar<EncoderKey> implements EncoderKey {
        CompositeEncoderKey(Iterable<EncoderKey> keys) {
            super(keys);
        }
    }

    private class CompositeDecoderKey extends CompositeSimilar<DecoderKey> implements DecoderKey {
        CompositeDecoderKey(Iterable<DecoderKey> keys) {
            super(keys);
        }
    }
}
