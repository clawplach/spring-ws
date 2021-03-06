/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xml.namespace;

import javax.xml.namespace.QName;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import org.w3c.dom.Node;

/**
 * Helper class for using {@link QName}.
 *
 * @author Arjen Poutsma
 * @see javax.xml.namespace.QName
 * @since 1.0.0
 */
public abstract class QNameUtils {

    /** Indicates whether {@link QName} has a prefix. The first release of the class did not have this. */
    private static boolean qNameHasPrefix;

    static {
        try {
            QName.class.getDeclaredConstructor(new Class[]{String.class, String.class, String.class});
            qNameHasPrefix = true;
        }
        catch (NoSuchMethodException e) {
            qNameHasPrefix = false;
        }
    }

    /**
     * Creates a new <code>QName</code> with the given parameters. Sets the prefix if possible, i.e. if the
     * <code>QName(String, String, String)</code> constructor can be found. If this constructor is not available (as is
     * the case on older implementations of JAX-RPC), the prefix is ignored.
     *
     * @param namespaceUri namespace URI of the <code>QName</code>
     * @param localPart    local part of the <code>QName</code>
     * @param prefix       prefix of the <code>QName</code>. May be ignored.
     * @return the created <code>QName</code>
     * @see QName#QName(String,String,String)
     */
    public static QName createQName(String namespaceUri, String localPart, String prefix) {
        if (qNameHasPrefix) {
            return new QName(namespaceUri, localPart, prefix);
        }
        else {
            return new QName(namespaceUri, localPart);
        }
    }

    /**
     * Returns the prefix of the given <code>QName</code>. Returns the prefix if available, i.e. if the
     * <code>QName.getPrefix()</code> method can be found. If this method is not available (as is the case on older
     * implementations of JAX-RPC), an empty string is returned.
     *
     * @param qName the <code>QName</code> to return the prefix from
     * @return the prefix, if available, or an empty string
     * @see javax.xml.namespace.QName#getPrefix()
     */
    public static String getPrefix(QName qName) {
        return qNameHasPrefix ? qName.getPrefix() : "";
    }

    /**
     * Validates the given String as a QName
     *
     * @param text the qualified name
     * @return <code>true</code> if valid, <code>false</code> otherwise
     */
    public static boolean validateQName(String text) {
        if (!StringUtils.hasLength(text)) {
            return false;
        }
        if (text.charAt(0) == '{') {
            int i = text.indexOf('}');

            if (i == -1 || i == text.length() - 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the qualified name of the given DOM Node.
     *
     * @param node the node
     * @return the qualified name of the node
     */
    public static QName getQNameForNode(Node node) {
        if (node.getNamespaceURI() != null && node.getPrefix() != null && node.getLocalName() != null) {
            return createQName(node.getNamespaceURI(), node.getLocalName(), node.getPrefix());
        }
        else if (node.getNamespaceURI() != null && node.getLocalName() != null) {
            return new QName(node.getNamespaceURI(), node.getLocalName());
        }
        else if (node.getLocalName() != null) {
            return new QName(node.getLocalName());
        }
        else {
            // as a last resort, use the node name
            return new QName(node.getNodeName());
        }
    }

    /**
     * Convert a <code>QName</code> to a qualified name, as used by DOM and SAX. The returned string has a format of
     * <code>prefix:localName</code> if the prefix is set, or just <code>localName</code> if not.
     *
     * @param qName the <code>QName</code>
     * @return the qualified name
     */
    public static String toQualifiedName(QName qName) {
        String prefix = getPrefix(qName);
        if (!StringUtils.hasLength(prefix)) {
            return qName.getLocalPart();
        }
        else {
            return prefix + ":" + qName.getLocalPart();
        }
    }

    /**
     * Convert a namespace URI and DOM or SAX qualified name to a <code>QName</code>. The qualified name can have the
     * form <code>prefix:localname</code> or <code>localName</code>.
     *
     * @param namespaceUri  the namespace URI
     * @param qualifiedName the qualified name
     * @return a QName
     */
    public static QName toQName(String namespaceUri, String qualifiedName) {
        int idx = qualifiedName.indexOf(':');
        if (idx == -1) {
            return new QName(namespaceUri, qualifiedName);
        }
        else {
            return createQName(namespaceUri, qualifiedName.substring(idx + 1), qualifiedName.substring(0, idx));
        }
    }

    /**
     * Parse the given qualified name string into a <code>QName</code>. Expects the syntax <code>localPart</code>,
     * <code>{namespace}localPart</code>, or <code>{namespace}prefix:localPart</code>. This format resembles the
     * <code>toString()</code> representation of <code>QName</code> itself, but allows for prefixes to be specified as
     * well.
     *
     * @return a corresponding QName instance
     * @throws IllegalArgumentException when the given string is <code>null</code> or empty.
     */
    public static QName parseQNameString(String qNameString) {
        Assert.hasLength(qNameString, "QName text may not be null or empty");
        if (qNameString.charAt(0) != '{') {
            return new QName(qNameString);
        }
        else {
            int endOfNamespaceURI = qNameString.indexOf('}');
            if (endOfNamespaceURI == -1) {
                throw new IllegalArgumentException(
                        "Cannot create QName from \"" + qNameString + "\", missing closing \"}\"");
            }
            int prefixSeperator = qNameString.indexOf(':', endOfNamespaceURI + 1);
            String namespaceURI = qNameString.substring(1, endOfNamespaceURI);
            if (prefixSeperator == -1) {
                return new QName(namespaceURI, qNameString.substring(endOfNamespaceURI + 1));
            }
            else {
                return createQName(namespaceURI, qNameString.substring(prefixSeperator + 1),
                        qNameString.substring(endOfNamespaceURI + 1, prefixSeperator));
            }
        }

    }

}
