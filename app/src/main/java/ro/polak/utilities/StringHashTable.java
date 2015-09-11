package ro.polak.utilities;

import java.io.Serializable;
import java.util.Vector;

/**
 * Attribute list FIXME This is redundant, since Java provides HashMaps by
 * default
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.1/22.12.2010
 * 
 */
public class StringHashTable implements Serializable {

	static final long serialVersionUID = 1212334;
	private Vector<String> attributeNames = new Vector<String>(0);
	private Vector<String> attributeValues = new Vector<String>(0);

	/**
	 * Sets attribute
	 * 
	 * @param attributeName
	 *            attribute name
	 * @param attributeValue
	 *            attribute value
	 */
	public void set(String attributeName, String attributeValue) {
		int index = attributeNames.indexOf(attributeName);

		if (index != -1) {
			attributeValues.setElementAt(attributeValue, index);
		} else {
			try {
				attributeNames.addElement(attributeName);
				attributeValues.addElement(attributeValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns name at specified index
	 * 
	 * @param i
	 *            index
	 * @return value at name index
	 */
	public String getNameAt(int i) {
		return (String) attributeNames.elementAt(i);
	}

	/**
	 * Returns value at specified index
	 * 
	 * @param i
	 *            index
	 * @return value at specified index
	 */
	public String getValueAt(int i) {
		return (String) attributeValues.elementAt(i);
	}

	/**
	 * 
	 * @param attributeName
	 *            name of the attribute
	 * @return specified attribute value
	 */
	public String get(String attributeName) {
		int index = attributeNames.indexOf(attributeName);
		if (index == -1) {
			return null;
		}
		return (String) attributeValues.elementAt(index);
	}

	/**
	 * Returns size of the list
	 * 
	 * @return size of the list
	 */
	public int size() {
		return attributeNames.size();
	}
}
