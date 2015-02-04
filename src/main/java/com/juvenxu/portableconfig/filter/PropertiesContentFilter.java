package com.juvenxu.portableconfig.filter;

import com.juvenxu.portableconfig.ContentFilter;
import com.juvenxu.portableconfig.model.Replace;
import org.codehaus.plexus.component.annotations.Component;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * @author juven
 */
@Component(role = ContentFilter.class, hint = "properties")
public class PropertiesContentFilter extends LineBasedContentFilter
{
  @Override
  public boolean accept(String contentType)
  {
    return ".properties".equals(contentType);
  }

  @Override
  protected String filterLine(String line, List<Replace> replaces)
  {
    if (isComment(line))
    {
      return line;
    }

    final String equalsMark = "=";
    final int equalsMarkIndex = line.indexOf(equalsMark);
    final String colonMark = ":";
    final int colonMarkIndex = line.indexOf(colonMark);

    if (equalsMarkIndex != -1)
    {
      final String startMask = "${";
      final String endMask = "}";
      final int startDollarIndex = line.indexOf(startMask);
      final int endDollarIndex = line.indexOf(endMask);
      if (startDollarIndex != -1)
      {
        line = replaceLineWith(line, replaces, startDollarIndex, endDollarIndex);
      }
      return replaceLineWith(line, replaces, equalsMark, equalsMarkIndex);
    }

    if (colonMarkIndex != -1)
    {
      return replaceLineWith(line, replaces, colonMark, colonMarkIndex);
    }

    return line;
  }

  private String replaceLineWith(String line, List<Replace> replaces, String equalsMark, int equalsMarkIndex)
  {
    String key = line.substring(0, equalsMarkIndex).trim();

    for (final Replace replace : replaces)
    {
      if (replace.getKey().equals(key))
      {
        return key + equalsMark + replace.getValue();
      }
    }

    return line;
  }

  private String replaceLineWith(String line, List<Replace> replaces, int startMaskIndex, int endMaskIndex)
  {
    String key = line.substring(startMaskIndex + 2, endMaskIndex).trim();

    for (final Replace replace : replaces)
    {
      if (replace.getKey().equals(key))
      {
        return line.replace("${" + key + "}", replace.getValue());
      }
    }

    return line;
  }

  private boolean isComment(String line)
  {
    return line.startsWith("#") || line.startsWith("!");
  }

}
