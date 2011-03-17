//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.16 at 05:44:57 PM EDT 
//


package org.voltdb.compiler.deploymentfile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pathsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pathsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="voltroot" type="{}pathEntry"/>
 *         &lt;element name="snapshots" type="{}pathEntry" minOccurs="0"/>
 *         &lt;element name="partitiondetectionsnapshot" type="{}pathEntry" minOccurs="0"/>
 *         &lt;element name="exportoverflow" type="{}pathEntry" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pathsType", propOrder = {

})
public class PathsType {

    @XmlElement(required = true)
    protected PathEntry voltroot;
    protected PathEntry snapshots;
    protected PathEntry partitiondetectionsnapshot;
    protected PathEntry exportoverflow;

    /**
     * Gets the value of the voltroot property.
     * 
     * @return
     *     possible object is
     *     {@link PathEntry }
     *     
     */
    public PathEntry getVoltroot() {
        return voltroot;
    }

    /**
     * Sets the value of the voltroot property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathEntry }
     *     
     */
    public void setVoltroot(PathEntry value) {
        this.voltroot = value;
    }

    /**
     * Gets the value of the snapshots property.
     * 
     * @return
     *     possible object is
     *     {@link PathEntry }
     *     
     */
    public PathEntry getSnapshots() {
        return snapshots;
    }

    /**
     * Sets the value of the snapshots property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathEntry }
     *     
     */
    public void setSnapshots(PathEntry value) {
        this.snapshots = value;
    }

    /**
     * Gets the value of the partitiondetectionsnapshot property.
     * 
     * @return
     *     possible object is
     *     {@link PathEntry }
     *     
     */
    public PathEntry getPartitiondetectionsnapshot() {
        return partitiondetectionsnapshot;
    }

    /**
     * Sets the value of the partitiondetectionsnapshot property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathEntry }
     *     
     */
    public void setPartitiondetectionsnapshot(PathEntry value) {
        this.partitiondetectionsnapshot = value;
    }

    /**
     * Gets the value of the exportoverflow property.
     * 
     * @return
     *     possible object is
     *     {@link PathEntry }
     *     
     */
    public PathEntry getExportoverflow() {
        return exportoverflow;
    }

    /**
     * Sets the value of the exportoverflow property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathEntry }
     *     
     */
    public void setExportoverflow(PathEntry value) {
        this.exportoverflow = value;
    }

}
