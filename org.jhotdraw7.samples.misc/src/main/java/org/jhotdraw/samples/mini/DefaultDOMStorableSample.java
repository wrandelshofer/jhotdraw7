/* @(#)QuickAndDirtyDOMStorableSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.mini;

import org.jhotdraw.nanoxml.NanoXMLDOMInput;
import org.jhotdraw.nanoxml.NanoXMLDOMOutput;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;
import org.jhotdraw.xml.DefaultDOMFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@code QuickAndDirtyDOMStorableSample} serializes a DOMStorable MyObject into
 * a String using the DefaultDOMFactory and then deserializes it from the
 * String.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultDOMStorableSample {

    public static class MyObject implements DOMStorable {

        private String name;

        /**
         * DOM Storable objects must have a non-argument constructor.
         */
        public MyObject() {
        }

        public MyObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void write(DOMOutput out) throws IOException {
            out.addAttribute("name", name);
        }

        @Override
        public void read(DOMInput in) throws IOException {
            name = in.getAttribute("name", null);
        }
    }

    public static void main(String[] args) {
        try {
            // Set up the DefaultDOMFactory
            DefaultDOMFactory factory = new DefaultDOMFactory();
            factory.addStorableClass("MyElementName", MyObject.class);

            // Create a DOMStorable object
            MyObject obj = new MyObject("Hello World");
            System.out.println("The name of the original object is:" + obj.getName());

            // Write the object into a DOM, and then serialize the DOM into a String
            NanoXMLDOMOutput out = new NanoXMLDOMOutput(factory);
            out.writeObject(obj);

            StringWriter writer = new StringWriter();
            out.save(writer);
            String serializedString = writer.toString();

            System.out.println("\nThe serialized representation of the object is:\n" + serializedString);

            // Deserialize a DOM from a String, and then read the object from the DOM
            StringReader reader = new StringReader(serializedString);
            NanoXMLDOMInput in = new NanoXMLDOMInput(factory, reader);
            MyObject obj2 = (MyObject) in.readObject();

            System.out.println("\nThe name of the restored object is:" + obj2.getName());
        } catch (IOException ex) {
            Logger.getLogger(DefaultDOMStorableSample.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
