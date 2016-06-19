/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic.
 *
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.util;

import info.schnatterer.nusic.ui.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.util.Log;

public class TextUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TextUtil.class);

    /**
     * Regex that matches a resource string such as <code>@string/a-b_c1</code>.
     */
    private static final String REGEX_RESOURCE_STRING = "@string/([A-Za-z0-9-_]*)";
    /**
     * Regex that matches file names case insensitively that have common file
     * extensions for HTML.
     */
    private static final String REGEX_ENDING_HTML = "(?i)^.*(\\.htm[l]?)$";

    private TextUtil() {
    }

    /**
     * Convenience method for
     * {@link #loadTextFromAsset(Context, String, boolean)} that does <i>not</i>
     * try to replace application string resources (e.g.
     * <code>@string/abc</code>)in input
     *
     * @param context
     * @param assetPath
     * @return
     */
    public static CharSequence loadTextFromAsset(Context context,
            String assetPath) {
        return loadTextFromAsset(context, assetPath, false);
    }

    /**
     * Tries to load an asset file as text. If <code>assetPath</code> ends in
     * <code>.html</code>, the HTML code is rendered into "displayable styled"
     * text.
     *
     * @param context
     *            context to load asset and (potential resources) from
     * @param assetPath
     *            path of the asset to load
     * @param replaceResources
     *            if <code>true</code>, resources such as
     *            <code>@string/abc</code> are replaced with their localized
     *            values from the app's resource strings (e.g.
     *            <code>strings.xml</code>). Set to <code>false</code> for
     *            better performance.
     * @return (potentially styled) text from asset
     */
    public static CharSequence loadTextFromAsset(Context context,
            String assetPath, boolean replaceResources) {
        if (assetPath != null) {
            InputStream is = null;
            try {
                is = context.getResources().getAssets().open(assetPath);
                String assetText = IOUtils.toString(is);
                if (assetPath.matches(REGEX_ENDING_HTML)) {
                    return fromHtml(replaceResourceStrings(context, assetText));
                } else {
                    return assetText;
                }
            } catch (IOException e) {
                LOG.warn(
                        "Unable to load asset from path \"" + assetPath + "\"",
                        e);
                return context
                        .getString(R.string.TextAssetActivity_errorLoadingFile);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return null;
    }

    /**
     * Recursively replaces resources such as <code>@string/abc</code> with
     * their localized values from the app's resource strings (e.g.
     * <code>strings.xml</code>) within a <code>source</code> string.
     *
     * Also works recursively, that is, when a resource contains another
     * resource that contains another resource, etc.
     *
     * @param context
     * @param source
     * @return <code>source</code> with replaced resources (if they exist)
     */
    public static String replaceResourceStrings(Context context, String source) {
        // Recursively resolve strings
        Pattern p = Pattern.compile(REGEX_RESOURCE_STRING);
        Matcher m = p.matcher(source);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String stringFromResources = ResourceUtil.getStringByName(context,
                    m.group(1));
            if (stringFromResources == null) {
                LOG.warn("No String resource found for ID \"" + m.group(1)
                        + "\" while inserting resources");
                /*
                 * No need to try to load from defaults, android is trying that
                 * for us. If we're here, the resource does not exist. Just
                 * return its ID.
                 */
                stringFromResources = m.group(1);
            }
            m.appendReplacement(sb, // Recurse
                    replaceResourceStrings(context, stringFromResources));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Similar to {@link android.text.Html#fromHtml(String)}, but provides
     * support for more HTML-tags such as lists.
     *
     * @param string
     * @return
     */
    public static CharSequence fromHtml(String string) {
        return Html.fromHtml(string.replaceFirst("<title>.*</title>", ""),
                null, new MyTagHandler());
    }

    /**
     * Copyright (C) 2013-2014 Juha Kuitunen Copyright (C) 2007 The Android Open
     * Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License"); you may
     * not use this file except in compliance with the License. You may obtain a
     * copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
     * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
     * License for the specific language governing permissions and limitations
     * under the License.
     */
    /**
     * Implements support for ordered and unordered lists in to Android
     * TextView.
     *
     * Some code taken from inner class
     * android.text.Html.HtmlToSpannedConverter. If you find this code useful,
     * please vote my answer at <a
     * href="http://stackoverflow.com/a/17365740/262462">StackOverflow</a> up.
     *
     * @version v1.0.1
     */
    private static class MyTagHandler implements Html.TagHandler {
        /**
         * A list of tags that do not influence rendering. We don't want to have
         * them on the log as unsupported tags.
         */
        protected static final Set<String> IGNORED_TAGS = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList("html", "head", "body")));
        /**
        /**
         * Keeps track of lists (ol, ul). On bottom of Stack is the outermost
         * list and on top of Stack is the most nested list
         */
        Stack<String> lists = new Stack<String>();
        /**
         * Tracks indexes of ordered lists so that after a nested list ends we
         * can continue with correct index of outer list
         */
        Stack<Integer> olNextIndex = new Stack<Integer>();
        /**
         * List indentation in pixels. Nested lists use multiple of this.
         */
        private static final int indent = 10;
        private static final int listItemIndent = indent * 2;
        private static final BulletSpan bullet = new BulletSpan(indent);

        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                XMLReader xmlReader) {
            if (tag.equalsIgnoreCase("ul")) {
                if (opening) {
                    lists.push(tag);
                } else {
                    lists.pop();
                }
            } else if (tag.equalsIgnoreCase("ol")) {
                if (opening) {
                    lists.push(tag);
                    // TODO: add support for lists starting other index than 1
                    olNextIndex.push(Integer.valueOf(1)).toString();
                } else {
                    lists.pop();
                    olNextIndex.pop().toString();
                }
            } else if (tag.equalsIgnoreCase("li")) {
                if (opening) {
                    if (output.length() > 0
                            && output.charAt(output.length() - 1) != '\n') {
                        output.append("\n");
                    }
                    String parentList = lists.peek();
                    if (parentList.equalsIgnoreCase("ol")) {
                        start(output, new Ol());
                        output.append(olNextIndex.peek().toString() + ". ");
                        olNextIndex.push(Integer.valueOf(olNextIndex.pop()
                                .intValue() + 1));
                    } else if (parentList.equalsIgnoreCase("ul")) {
                        start(output, new Ul());
                    }
                } else {
                    if (lists.peek().equalsIgnoreCase("ul")) {
                        if (output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                        // Nested BulletSpans increases distance between bullet
                        // and text, so we must prevent it.
                        int bulletMargin = indent;
                        if (lists.size() > 1) {
                            bulletMargin = indent
                                    - bullet.getLeadingMargin(true);
                            if (lists.size() > 2) {
                                // This get's more complicated when we add a
                                // LeadingMarginSpan into the same line:
                                // we have also counter it's effect to
                                // BulletSpan
                                bulletMargin -= (lists.size() - 2)
                                        * listItemIndent;
                            }
                        }
                        BulletSpan newBullet = new BulletSpan(bulletMargin);
                        end(output, Ul.class, new LeadingMarginSpan.Standard(
                                listItemIndent * (lists.size() - 1)), newBullet);
                    } else if (lists.peek().equalsIgnoreCase("ol")) {
                        if (output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                        int numberMargin = listItemIndent * (lists.size() - 1);
                        if (lists.size() > 2) {
                            // Same as in ordered lists: counter the effect of
                            // nested Spans
                            numberMargin -= (lists.size() - 2) * listItemIndent;
                        }
                        end(output, Ol.class, new LeadingMarginSpan.Standard(
                                numberMargin));
                    }
                }
            } else {
                if (opening) {
                    // Log unknown tags
                    if (!IGNORED_TAGS.contains(tag))
                        Log.d("TagHandler", "Found an unsupported tag " + tag);
                }
            }
        }

        /** @see android.text.Html */
        private static void start(Editable text, Object mark) {
            int len = text.length();
            text.setSpan(mark, len, len, Spanned.SPAN_MARK_MARK);
        }

        /** @see android.text.Html */
        private static void end(Editable text, Class<?> kind,
                Object... replaces) {
            int len = text.length();
            Object obj = getLast(text, kind);
            int where = text.getSpanStart(obj);
            text.removeSpan(obj);
            if (where != len) {
                for (Object replace : replaces) {
                    text.setSpan(replace, where, len,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return;
        }

        /** @see android.text.Html */
        private static Object getLast(Spanned text, Class<?> kind) {
            /*
             * This knows that the last returned object from getSpans() will be
             * the most recently added.
             */
            Object[] objs = text.getSpans(0, text.length(), kind);
            if (objs.length == 0) {
                return null;
            }
            return objs[objs.length - 1];
        }

        private static class Ul {
        }

        private static class Ol {
        }

    }
}
