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
package org.n52.sos.ogc.filter;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.StringHelper;

/**
 * SOS class for result filter
 * 
 * @since 4.0.0
 */
public class ComparisonFilter extends Filter<ComparisonOperator> {

    /**
     * Filter operator
     */
    private ComparisonOperator operator;

    /**
     * filter value
     */
    private String value;

    /**
     * filter value for between filter
     */
    private String valueUpper;

    /**
     * escape character
     */
    private String escapeString;

    /**
     * wild card character
     */
    private String wildCard;

    /**
     * single char character
     */
    private String singleChar;

    /**
     * default constructor
     */
    public ComparisonFilter() {
    }

    /**
     * constructor
     * 
     * @param operator
     *            Filter operator
     * @param valueReference
     *            valueReference
     * @param value
     *            value
     */
    public ComparisonFilter(ComparisonOperator operator, String valueReference, String value) {
        super(valueReference);
        this.operator = operator;
        this.value = value;
    }

    /**
     * constructor for {@link ComparisonOperator#PropertyIsBetween} filter
     * 
     * @param operator
     *            Filter operator
     * @param valueReference
     *            valueReference
     * @param value
     *            value
     * @param valueUpper
     *            upper value
     * @throws OwsExceptionReport
     *             If operator is not
     *             {@link ComparisonOperator#PropertyIsBetween}
     */
    public ComparisonFilter(ComparisonOperator operator, String valueReference, String value, String valueUpper)
            throws OwsExceptionReport {
        super(valueReference);
        if (operator == ComparisonOperator.PropertyIsBetween) {
            this.operator = operator;
            this.value = value;
            this.valueUpper = valueUpper;
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Use other constructor for ComparisonFilter! This constructor could only"
                            + "be used for operator 'PropertyIsBetween'");
        }
    }

    /**
     * constructor for {@link ComparisonOperator#PropertyIsLike} filter
     * 
     * @param operator
     *            Filter operator
     * @param valueReference
     *            valueReference
     * @param value
     *            value
     * @param valueUpper
     *            upper value for between filter
     * @param escapeString
     *            Escape characters
     * @throws OwsExceptionReport
     *             If operator is not {@link ComparisonOperator#PropertyIsLike}
     */
    public ComparisonFilter(ComparisonOperator operator, String valueReference, String value, String valueUpper,
            String escapeString) throws OwsExceptionReport {
        super(valueReference);
        if (operator == ComparisonOperator.PropertyIsLike) {
            this.operator = operator;
            this.value = value;
            this.valueUpper = valueUpper;
            this.escapeString = escapeString;
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Use other constructor for ComparisonFilter! This constructor could only be used for operator 'PropertyIsLike'");
        }
    }

    @Override
    public ComparisonOperator getOperator() {
        return operator;
    }

    @Override
    public ComparisonFilter setOperator(ComparisonOperator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * Get filter value
     * 
     * @return filter value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get upper filter value
     * 
     * @return upper filter value
     */
    public String getValueUpper() {
        return valueUpper;
    }

    /**
     * Set filter value
     * 
     * @param value
     *            filter value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Set upper filter value
     * 
     * @param valueUpper
     *            upper filter value
     */
    public void setValueUpper(String valueUpper) {
        this.valueUpper = valueUpper;
    }

    /**
     * Get escape characters
     * 
     * @return escape characters
     */
    public String getEscapeString() {
        return escapeString;
    }

    /**
     * Set escape characters
     * 
     * @param escapeString
     *            escape characters
     */
    public void setEscapeString(String escapeString) {
        this.escapeString = escapeString;
    }

    /**
     * Get wild card character
     * 
     * @return wild card character
     */
    public String getWildCard() {
        return wildCard;
    }

    /**
     * Set wild card character
     * 
     * @param wildCard
     *            wild card character
     */
    public void setWildCard(String wildCard) {
        this.wildCard = wildCard;
    }

    /**
     * Get single char character
     * 
     * @return single char character
     */
    public String getSingleChar() {
        return singleChar;
    }

    /**
     * Set single char character
     * 
     * @param singleChar
     *            single char character
     */
    public void setSingleChar(String singleChar) {
        this.singleChar = singleChar;
    }
    
    /**
     * Check if value is not null or empty
     * 
     * @return <code>true</code>, if value is not empty
     */
    public boolean isSetValue() {
        return StringHelper.isNotEmpty(getValue());
    }

    /**
     * Check if value upper is not null or empty
     * 
     * @return <code>true</code>, if value upper is not empty
     */
    public boolean isSetValueUpper() {
        return StringHelper.isNotEmpty(getValueUpper());
    }

    /**
     * Check if escape string is not null or empty
     * 
     * @return <code>true</code>, if escape string is not empty
     */
    public boolean isSetEscapeString() {
        return StringHelper.isNotEmpty(getEscapeString());
    }
    
    /**
     * Check if wild card is not null or empty
     * 
     * @return <code>true</code>, if wild card is not empty
     */
    public boolean isSetWildCard() {
        return StringHelper.isNotEmpty(getWildCard());
    }
    
    /**
     * Check if single char is not null or empty
     * 
     * @return <code>true</code>, if single char is not empty
     */
    public boolean isSetSingleChar() {
        return StringHelper.isNotEmpty(getSingleChar());
    }
    
    @Override
    public String toString() {
        String result = "ComparisonFilter: ";
        if (isSetValueUpper()) {
            return result + getValueReference() + " " + getValue() + " " + getOperator().name() + " "
                    + getValueUpper();
        } else {
            return result + getValueReference() + " " + getOperator().name() + " " + getValue();
        }
    }


}
