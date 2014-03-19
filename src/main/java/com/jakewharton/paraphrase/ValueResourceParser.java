package com.jakewharton.paraphrase;

import com.android.annotations.NonNull;
import com.android.ide.common.res2.MergingException;
import com.android.ide.common.res2.ResourceItem;
import com.android.resources.ResourceType;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import static com.android.SdkConstants.ATTR_NAME;
import static com.android.SdkConstants.ATTR_TYPE;
import static com.android.SdkConstants.TAG_ITEM;

// Mostly stolen from com.android.ide.common.res2.ValueResourceParser2
final class ValueResourceParser {
  static List<Phrase> parse(File file) throws MergingException {
    return new ValueResourceParser(file).parseFile();
  }

  private final File file;

  private ValueResourceParser(File file) {
    this.file = file;
  }

  /**
   * Parses the file and returns a list of {@link ResourceItem} objects.
   * @return a list of resources.
   *
   * @throws MergingException if a merging exception happens
   */
  private @NonNull List<Phrase> parseFile() throws MergingException {
    Document document = parseDocument(file);

    // get the root node
    Node rootNode = document.getDocumentElement();
    if (rootNode == null) {
      return Collections.emptyList();
    }
    NodeList nodes = rootNode.getChildNodes();

    final int count = nodes.getLength();
    // list containing the result
    List<Phrase> phrases = Lists.newArrayListWithExpectedSize(count);
    // string that contains the documentation for the next item
    String nextDocumentation = null;

    for (int i = 0, n = nodes.getLength(); i < n; i++) {
      Node node = nodes.item(i);

      if (node.getNodeType() == Node.COMMENT_NODE) {
        nextDocumentation = node.getTextContent(); //TODO validate?
      } else if (node.getNodeType() == Node.ELEMENT_NODE) {
        String documentation = nextDocumentation;
        // We are in a new item, so let's clear the value of nextDocumentation.
        nextDocumentation = null;

        ResourceItem resource = getResource(node);
        if (resource != null && ResourceType.STRING.equals(resource.getType())) {
          String name = resource.getName(); //TODO validate?
          String value = resource.getValueText();
          if (value != null && Phrase.isPhrase(value)) {
            phrases.add(Phrase.from(name, documentation, value));
          }
        }
      }
    }

    return phrases;
  }

  private static Document parseDocument(File file) throws MergingException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    BufferedInputStream stream = null;
    try {
      stream = new BufferedInputStream(new FileInputStream(file));
      InputSource is = new InputSource(stream);
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.parse(is);
    } catch (SAXParseException e) {
      String message = e.getLocalizedMessage();
      MergingException exception = new MergingException(message, e);
      exception.setFile(file);
      int lineNumber = e.getLineNumber();
      if (lineNumber != -1) {
        exception.setLine(lineNumber - 1); // make line numbers 0-based
        exception.setColumn(e.getColumnNumber() - 1);
      }
      throw exception;
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new MergingException(e).setFile(file);
    } finally {
      Closeables.closeQuietly(stream);
    }
  }


  /**
   * Returns a new ResourceItem object for a given node.
   * @param node the node representing the resource.
   * @return a ResourceItem object or null.
   */
  static ResourceItem getResource(Node node) {
    ResourceType type = getType(node);
    String name = getName(node);

    if (type != null && name != null) {
      return new ResourceItem(name, type, node);
    }

    return null;
  }

  /**
   * Returns the type of the ResourceItem based on a node's attributes.
   * @param node the node
   * @return the ResourceType or null if it could not be inferred.
   */
  static ResourceType getType(Node node) {
    String nodeName = node.getLocalName();
    String typeString = null;

    if (TAG_ITEM.equals(nodeName)) {
      Attr attribute = (Attr) node.getAttributes().getNamedItemNS(null, ATTR_TYPE);
      if (attribute != null) {
        typeString = attribute.getValue();
      }
    } else {
      // the type is the name of the node.
      typeString = nodeName;
    }

    if (typeString != null) {
      return ResourceType.getEnum(typeString);
    }

    return null;
  }

  /**
   * Returns the name of the resource based a node's attributes.
   * @param node the node.
   * @return the name or null if it could not be inferred.
   */
  static String getName(Node node) {
    Attr attribute = (Attr) node.getAttributes().getNamedItemNS(null, ATTR_NAME);

    if (attribute != null) {
      return attribute.getValue();
    }

    return null;
  }
}
