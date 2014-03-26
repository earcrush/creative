/* Copyright ItsOn Inc. 2010 */
package com.earcrush.common.util;

import org.apache.commons.lang3.StringUtils;


/**
 * String utilities
 *
 * @author Charles Hudak
 * @since Jul 26, 2010
 *
 */
public class StringUtil
{
    public static String normalizeNewlines(String input)
    {
        return StringUtils.replace(StringUtils.replace(input, "\r\n", "\n"), "\r", "\n");
    }
}
