/* @(#)NanoXMLDOMInput.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.nanoxml;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLParserFactory;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Disposable;
import org.jhotdraw.xml.DOMFactory;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMStorable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * NanoXMLDOMInput.
 * <p>
 * Design pattern:<br>
 * Name: Adapter.<br>
 * Role: Adapter.<br>
 * Partners: {@link net.n3.nanoxml.XMLElement} as Adaptee.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NanoXMLDOMInput implements DOMInput, Disposable {
    /**
     * This map is used to unmarshall references to objects to
     * the XML DOM. A key in this map is a String representing a marshalled
     * reference. A value in this map is an unmarshalled Object.
     */
    private HashMap<String, Object> idobjects = new HashMap<String, Object>();

    /**
     * The document used for input.
     */
    @Nullable
    private XMLElement document;
    /**
     * The current node used for input.
     */
    private IXMLElement current;

    /**
     * The factory used to create objects from XML tag names.
     */
    private DOMFactory factory;

    /**
     * The stack.
     */
    private Stack<IXMLElement> stack = new Stack<IXMLElement>();

    public NanoXMLDOMInput(DOMFactory factory, InputStream in) throws IOException {
        this(factory, new InputStreamReader(in, "UTF8"));
    }

    public NanoXMLDOMInput(DOMFactory factory, Reader in) throws IOException {
        this.factory = factory;

        try {

            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = new StdXMLReader(in);
            parser.setReader(reader);
            document = new XMLElement();
            current = (XMLElement) parser.parse();
            if (current != null) {
                document.addChild(current);
            }
            current = document;
        } catch (Exception e) {
            IOException error = new IOException(e.getMessage());
            error.initCause(e);
            throw error;
        }
    }

    /**
     * Returns the tag name of the current element.
     */
    @Override
    public String getTagName() {
        return current.getName();
    }

    /**
     * Gets an attribute of the current element of the DOM Document.
     */
    @Override
    public String getAttribute(String name, String defaultValue) {
        return current.getAttribute(name, defaultValue);
    }

    /**
     * Gets an attribute of the current element of the DOM Document and of
     * all parent DOM elements.
     */
    @Override
    public java.util.List<String> getInheritedAttribute(String name) {
        LinkedList<String> values = new LinkedList<String>();
        for (IXMLElement node : stack) {
            String value = node.getAttribute(name, null);
            values.add(value);
        }
        String value = current.getAttribute(name, null);
        values.add(value);
        return values;
    }

    /**
     * Gets the text of the current element of the DOM Document.
     */
    @Override
    public String getText() {
        return getText(null);
    }

    /**
     * Gets the text of the current element of the DOM Document.
     */
    @Override
    public String getText(String defaultValue) {
        String value = current.getContent();
        return (value == null) ? defaultValue : value;
    }

    /**
     * Gets an attribute of the current element of the DOM Document.
     */
    @Override
    public int getAttribute(String name, int defaultValue) {
        String value = current.getAttribute(name, null);
        return (value == null || value.length() == 0) ? defaultValue : Long.decode(value).intValue();
    }

    /**
     * Gets an attribute of the current element of the DOM Document.
     */
    @Override
    public double getAttribute(String name, double defaultValue) {
        String value = current.getAttribute(name, null);
        return (value == null || value.length() == 0) ? defaultValue : Double.parseDouble(value);
    }

    /**
     * Gets an attribute of the current element of the DOM Document.
     */
    @Override
    public boolean getAttribute(String name, boolean defaultValue) {
        String value = current.getAttribute(name, null);
        return (value == null || value.length() == 0) ? defaultValue : Boolean.valueOf(value).booleanValue();
    }


    /**
     * Returns the number of child elements of the current element.
     */
    @Override
    public int getElementCount() {
        return current.getChildrenCount();
    }

    /**
     * Returns the number of child elements with the specified tag name
     * of the current element.
     */
    @Override
    public int getElementCount(String tagName) {
        int count = 0;
        ArrayList<IXMLElement> list = current.getChildren();
        for (IXMLElement node : list) {
            if (node.getName().equals(tagName)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Opens the element with the specified index and makes it the current node.
     */
    @Override
    public void openElement(int index) {
        stack.push(current);
        ArrayList<IXMLElement> list = current.getChildren();
        current = list.get(index);
    }

    /**
     * Opens the last element with the specified name and makes it the current node.
     */
    @Override
    public void openElement(String tagName) throws IOException {
        ArrayList<IXMLElement> list = current.getChildren();
        for (IXMLElement node : list) {
            if (node.getName().equals(tagName)) {
                stack.push(current);
                current = node;
                return;
            }
        }
        throw new IOException("no such element:" + tagName);
    }

    /**
     * Opens the element with the specified name and index and makes it the
     * current node.
     */
    @Override
    public void openElement(String tagName, int index) throws IOException {
        int count = 0;
        ArrayList<IXMLElement> list = current.getChildren();
        for (IXMLElement node : list) {
            if (node.getName().equals(tagName)) {
                if (count++ == index) {
                    stack.push(current);
                    current = node;
                    return;
                }
            }
        }
        throw new IOException("no such element:" + tagName + " at index:" + index);
    }

    /**
     * Closes the current element of the DOM Document.
     * The parent of the current element becomes the current element.
     *
     * @throws IllegalArgumentException if the provided tagName does
     *                                  not match the tag name of the element.
     */
    @Override
    public void closeElement() {
        current = (XMLElement) stack.pop();
    }

    /**
     * Reads an object from the current element.
     */
    @Override
    public Object readObject() throws IOException {
        return readObject(0);
    }

    /**
     * Reads an object from the current element.
     */
    @Override
    public Object readObject(int index) throws IOException {
        openElement(index);
        Object o;

        String ref = getAttribute("ref", null);
        String id = getAttribute("id", null);

        if (ref != null && id != null) {
            throw new IOException("Element has both an id and a ref attribute: <" + getTagName() + " id=\"" + id + "\" ref=\"" + ref + "\"> in line number " + current.getLineNr());
        }
        if (id != null && idobjects.containsKey(id)) {
            throw new IOException("Duplicate id attribute: <" + getTagName() + " id=\"" + id + "\"> in line number " + current.getLineNr());
        }
        if (ref != null && !idobjects.containsKey(ref)) {
            throw new IOException("Referenced element not found: <" + getTagName() + " ref=\"" + ref + "\"> in line number " + current.getLineNr());
        }

        // Keep track of objects which have an ID
        if (ref != null) {
            o = idobjects.get(ref);
        } else {
            o = factory.read(this);
            if (id != null) {
                idobjects.put(id, o);
            }
            if (o instanceof DOMStorable) {
                ((DOMStorable) o).read(this);
            }
        }

        closeElement();
        return o;
    }

    @Override
    public void dispose() {
        if (document != null) {
            document.dispose();
            document = null;
        }
    }
}
