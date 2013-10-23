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
package org.n52.sos.ogc.om.quality;

/**
 * class which represents a simple quantitative data quality element
 * 
 * @since 4.0.0
 */
public class SosQuality {

    /** name of the result value */
    private String resultName;

    /** unit of the result value */
    private String resultUnit;

    /** value of the quality result */
    private String resultValue;

    /** type of the quality object */
    private QualityType qualityType;

    /**
     * constructor
     * 
     * @param resultName
     *            Result name
     * @param resultUnit
     *            Result unit
     * @param resultValue
     *            Result value
     * @param qualityType
     *            Quality type
     */
    public SosQuality(String resultName, String resultUnit, String resultValue, QualityType qualityType) {
        this.resultName = resultName;
        this.resultUnit = resultUnit;
        this.resultValue = resultValue;
        this.qualityType = qualityType;
    }

    /**
     * Get value
     * 
     * @return the resultValue
     */
    public String getResultValue() {
        return resultValue;
    }

    /**
     * Set value
     * 
     * @param resultValue
     *            the resultValue to set
     */
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    /**
     * Get name
     * 
     * @return the resultName
     */
    public String getResultName() {
        return resultName;
    }

    /**
     * Set name
     * 
     * @param resultName
     *            the resultName to set
     */
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    /**
     * Get unit
     * 
     * @return the resultValueUnit
     */
    public String getResultUnit() {
        return resultUnit;
    }

    /**
     * Set unit
     * 
     * @param resultValueUnit
     *            the resultValueUnit to set
     */
    public void setResultUnit(String resultValueUnit) {
        this.resultUnit = resultValueUnit;
    }

    /**
     * Get quality type
     * 
     * @return the qualityType
     */
    public QualityType getQualityType() {
        return qualityType;
    }

    /**
     * Set quality type
     * 
     * @param qualityType
     *            the qualityType to set
     */
    public void setQualityType(QualityType qualityType) {
        this.qualityType = qualityType;
    }

    /**
     * quality type
     * 
     * @since 4.0.0
     */
    public enum QualityType {
        quantity, category, text
    }

}
