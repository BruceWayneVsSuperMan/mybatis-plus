/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import com.baomidou.mybatisplus.core.toolkit.support.BiIntFunction;
import jdk.internal.vm.annotation.ForceInline;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

/**
 * String 工具类
 *
 * @author D.Yang
 * @author hcl
 * @since 2016-08-18
 */
public final class StringUtils {

    /**
     * 空字符
     */
    public static final String EMPTY = StringPool.EMPTY;
    /**
     * 字符串 is
     */
    public static final String IS = "is";
    /**
     * 下划线字符
     */
    public static final char UNDERLINE = '_';
    /**
     * MP 内定义的 SQL 占位符表达式，匹配诸如 {0},{1},{2} ... 的形式
     */
    public final static Pattern MP_SQL_PLACE_HOLDER = Pattern.compile("[{](?<idx>\\d+)}");
    /**
     * 验证字符串是否是数据库字段
     */
    private static final Pattern P_IS_COLUMN = Pattern.compile("^\\w\\S*[\\w\\d]*$");

    /**
     * 是否为大写命名
     */
    private static final Pattern CAPITAL_MODE = Pattern.compile("^[0-9A-Z/_]+$");

    private StringUtils() {
    }

    /**
     * 安全的进行字符串 format
     *
     * @param target 目标字符串
     * @param params format 参数
     * @return format 后的
     */
    @ForceInline
    public static String format(String target, Object... params) {
        return String.format(target, params);
    }

    /**
     * 判断字符串是否为空
     *
     * @param cs 需要判断字符串
     * @return 判断结果
     */
    public static boolean isEmpty(CharSequence cs) {
        return null == cs || 0 == cs.length();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param cs 需要判断字符串
     * @return 判断结果
     */
    @ForceInline
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 猜测方法属性对应的 Getter 名称，具体规则请参考 JavaBeans 规范
     *
     * @param name 属性名称
     * @param type 属性类型
     * @return 返回猜测的名称
     */
    public static String guessGetterName(String name, Class<?> type) {
        if (ClassUtils.isBoolean(type)) {
            return name.startsWith("is") ? name : "is" + upperFirst(name);
        }
        return "get" + upperFirst(name);
    }

    /**
     * 大写第一个字母
     *
     * @param src 源字符串
     * @return 返回第一个大写后的字符串
     */
    public static String upperFirst(String src) {
        if (Character.isLowerCase(src.charAt(0))) {
            return 1 == src.length() ? src.toUpperCase() : Character.toUpperCase(src.charAt(0)) + src.substring(1);
        }
        return src;
    }


    /**
     * 判断字符串是不是驼峰命名
     *
     * <li> 包含 '_' 不算 </li>
     * <li> 首字母大写的不算 </li>
     *
     * @param str 字符串
     * @return 结果
     */
    public static boolean isCamel(String str) {
        if (str.contains(StringPool.UNDERSCORE)) {
            return false;
        }
        return Character.isLowerCase(str.charAt(0));
    }


    /**
     * 判断字符串是否符合数据库字段的命名
     *
     * @param str 字符串
     * @return 判断结果
     */
    public static boolean isNotColumnName(String str) {
        return !P_IS_COLUMN.matcher(str).matches();
    }

    /**
     * 获取真正的字段名
     *
     * @param column 字段名
     * @return 字段名
     */
    public static String getTargetColumn(String column) {
        if (isNotColumnName(column)) {
            return column.substring(1, column.length() - 1);
        }
        return column;
    }

    /**
     * 字符串驼峰转下划线格式
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String camelToUnderline(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 字符串下划线转驼峰格式
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String underlineToCamel(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        String temp = param.toLowerCase();
        int len = temp.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = temp.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(temp.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 首字母转换小写
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String firstToLowerCase(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }

    /**
     * 正则表达式匹配
     *
     * @param regex 正则表达式字符串
     * @param input 要匹配的字符串
     * @return 如果 input 符合 regex 正则表达式格式, 返回true, 否则返回 false;
     */
    public static boolean matches(String regex, String input) {
        if (null == regex || null == input) {
            return false;
        }
        return Pattern.matches(regex, input);
    }

    /**
     * 替换 SQL 语句中的占位符，例如输入 SELECT * FROM test WHERE id = {0} AND name = {1} 会被替换为
     * SELECT * FROM test WHERE id = 1 AND name = 'MP'
     * <p>
     * 当数组中参数不足时，该方法会抛出错误：数组下标越界{@link ArrayIndexOutOfBoundsException}
     * </p>
     *
     * @param content 填充内容
     * @param args    填充参数
     */
    public static String sqlArgsFill(String content, Object... args) {
        if (StringUtils.isNotEmpty(content) && ArrayUtils.isNotEmpty(args)) {
            // 索引不能使用，因为 SQL 中的占位符数字与索引不相同
            BiIntFunction<Matcher, CharSequence> handler = (m, i) -> sqlParam(args[Integer.parseInt(m.group("idx"))]);
            return replace(content, MP_SQL_PLACE_HOLDER, handler).toString();
        }
        return content;
    }

    /**
     * 根据指定的表达式替换字符串中指定格式的部分
     * <p>
     * BiIntFunction 中的 第二个 参数将传递 参数在字符串中的索引
     * </p>
     *
     * @param src      源字符串
     * @param ptn      需要替换部分的正则表达式
     * @param replacer 替换处理器
     * @return 返回字符串构建起
     */
    public static StringBuilder replace(CharSequence src, Pattern ptn, BiIntFunction<Matcher, CharSequence> replacer) {
        int idx = 0, last = 0, len = src.length();
        Matcher m = ptn.matcher(src);
        StringBuilder sb = new StringBuilder();

        // 扫描一次字符串
        while (m.find()) {
            sb.append(src, last, m.start()).append(replacer.apply(m, idx++));
            last = m.end();
        }
        // 如果表达式没有匹配或者匹配未到末尾，该判断保证字符串完整性
        if (last < len) {
            sb.append(src, last, len);
        }

        return sb;
    }

    /**
     * 获取SQL PARAMS字符串
     *
     * @param obj
     * @return
     */
    public static String sqlParam(Object obj) {
        String repStr;
        if (obj instanceof Collection) {
            repStr = StringUtils.quotaMarkList((Collection<?>) obj);
        } else {
            repStr = StringUtils.quotaMark(obj);
        }
        return repStr;
    }

    /**
     * 使用单引号包含字符串
     *
     * @param obj 原字符串
     * @return 单引号包含的原字符串
     */
    public static String quotaMark(Object obj) {
        String srcStr = String.valueOf(obj);
        if (obj instanceof CharSequence) {
            // fix #79
            return StringEscape.escapeString(srcStr);
        }
        return srcStr;
    }

    /**
     * 使用单引号包含字符串
     *
     * @param coll 集合
     * @return 单引号包含的原字符串的集合形式
     */
    public static String quotaMarkList(Collection<?> coll) {
        return coll.stream().map(StringUtils::quotaMark)
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    /**
     * 拼接字符串第二个字符串第一个字母大写
     */
    public static String concatCapitalize(String concatStr, final String str) {
        if (isEmpty(concatStr)) {
            concatStr = EMPTY;
        }
        if (str == null || str.length() == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return concatStr + Character.toTitleCase(firstChar) + str.substring(1);
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
     * @return ignore
     */
    public static boolean checkValNotNull(Object object) {
        if (object instanceof CharSequence) {
            return isNotEmpty((CharSequence) object);
        }
        return object != null;
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
     * @return ignore
     */
    public static boolean checkValNull(Object object) {
        return !checkValNotNull(object);
    }

    /**
     * 包含大写字母
     *
     * @param word 待判断字符串
     * @return ignore
     */
    public static boolean containsUpperCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为大写命名
     *
     * @param word 待判断字符串
     * @return ignore
     */
    public static boolean isCapitalMode(String word) {
        return null != word && CAPITAL_MODE.matcher(word).matches();
    }

    /**
     * 是否为驼峰下划线混合命名
     *
     * @param word 待判断字符串
     * @return ignore
     */
    public static boolean isMixedMode(String word) {
        return matches(".*[A-Z]+.*", word) && matches(".*[/_]+.*", word);
    }

    /**
     * Check if a String ends with a specified suffix.
     * <p>
     * <code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.
     * </p>
     * <p>
     * <pre>
     * StringUtils.endsWith(null, null)      = true
     * StringUtils.endsWith(null, "abcdef")  = false
     * StringUtils.endsWith("def", null)     = false
     * StringUtils.endsWith("def", "abcdef") = true
     * StringUtils.endsWith("def", "ABCDEF") = false
     * </pre>
     * </p>
     *
     * @param str    the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case
     * sensitive, or both <code>null</code>
     * @see String#endsWith(String)
     * @since 2.4
     */
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * Check if a String ends with a specified suffix (optionally case
     * insensitive).
     *
     * @param str        the String to check, may be null
     * @param suffix     the suffix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case (case
     *                   insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or both
     * <code>null</code>
     * @see String#endsWith(String)
     */
    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    /**
     * 是否为CharSequence类型
     *
     * @param clazz class
     * @return true 为是 CharSequence 类型
     */
    public static boolean isCharSequence(Class<?> clazz) {
        return clazz != null && CharSequence.class.isAssignableFrom(clazz);
    }

    /**
     * 去除boolean类型is开头的字符串
     *
     * @param propertyName 字段名
     * @param propertyType 字段类型
     */
    public static String removeIsPrefixIfBoolean(String propertyName, Class<?> propertyType) {
        if (ClassUtils.isBoolean(propertyType) && propertyName.startsWith(IS)) {
            String property = propertyName.replaceFirst(IS, EMPTY);
            if (isEmpty(property)) {
                return propertyName;
            } else {
                String firstCharToLowerStr = firstCharToLower(property);
                return property.equals(firstCharToLowerStr) ? propertyName : firstCharToLowerStr;
            }
        }
        return propertyName;
    }

    /**
     * 第一个首字母小写，之后字符大小写的不变
     * <p>StringUtils.firstCharToLower( "UserService" )     = userService</p>
     * <p>StringUtils.firstCharToLower( "UserServiceImpl" ) = userServiceImpl</p>
     *
     * @param rawString 需要处理的字符串
     * @return ignore
     */
    public static String firstCharToLower(String rawString) {
        return prefixToLower(rawString, 1);
    }

    /**
     * 前n个首字母小写,之后字符大小写的不变
     *
     * @param rawString 需要处理的字符串
     * @param index     多少个字符(从左至右)
     * @return ignore
     */
    public static String prefixToLower(String rawString, int index) {
        String beforeChar = rawString.substring(0, index).toLowerCase();
        String afterChar = rawString.substring(index);
        return beforeChar + afterChar;
    }

    /**
     * 删除字符前缀之后,首字母小写,之后字符大小写的不变
     * <p>StringUtils.removePrefixAfterPrefixToLower( "isUser", 2 )     = user</p>
     * <p>StringUtils.removePrefixAfterPrefixToLower( "isUserInfo", 2 ) = userInfo</p>
     *
     * @param rawString 需要处理的字符串
     * @param index     删除多少个字符(从左至右)
     * @return ignore
     */
    public static String removePrefixAfterPrefixToLower(String rawString, int index) {
        return prefixToLower(rawString.substring(index), 1);
    }

    /**
     * 驼峰转连字符
     * <p>StringUtils.camelToHyphen( "managerAdminUserService" ) = manager-admin-user-service</p>
     *
     * @param input ignore
     * @return 以'-'分隔
     * @see <a href="https://github.com/krasa/StringManipulation">document</a>
     */
    public static String camelToHyphen(String input) {
        return wordsToHyphenCase(wordsAndHyphenAndCamelToConstantCase(input));
    }

    private static String wordsAndHyphenAndCamelToConstantCase(String input) {
        boolean betweenUpperCases = false;
        boolean containsLowerCase = containsLowerCase(input);

        StringBuilder buf = new StringBuilder();
        char previousChar = ' ';
        char[] chars = input.toCharArray();
        for (char c : chars) {
            boolean isUpperCaseAndPreviousIsUpperCase = (Character.isUpperCase(previousChar)) && (Character.isUpperCase(c));
            boolean isUpperCaseAndPreviousIsLowerCase = (Character.isLowerCase(previousChar)) && (Character.isUpperCase(c));

            boolean previousIsWhitespace = Character.isWhitespace(previousChar);
            boolean lastOneIsNotUnderscore = (buf.length() > 0) && (buf.charAt(buf.length() - 1) != '_');
            boolean isNotUnderscore = c != '_';
            if (lastOneIsNotUnderscore && (isUpperCaseAndPreviousIsLowerCase || previousIsWhitespace)) {
                buf.append(StringPool.UNDERSCORE);
            } else if ((Character.isDigit(previousChar) && Character.isLetter(c))) {
                buf.append('_');
            }
            if ((shouldReplace(c)) && (lastOneIsNotUnderscore)) {
                buf.append('_');
            } else if (!Character.isWhitespace(c) && (isNotUnderscore || lastOneIsNotUnderscore)) {
                buf.append(Character.toUpperCase(c));
            }
            previousChar = c;
        }
        if (Character.isWhitespace(previousChar)) {
            buf.append(StringPool.UNDERSCORE);
        }
        return buf.toString();
    }

    public static boolean containsLowerCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldReplace(char c) {
        return (c == '.') || (c == '_') || (c == '-');
    }

    private static String wordsToHyphenCase(String s) {
        StringBuilder buf = new StringBuilder();
        char lastChar = 'a';
        for (char c : s.toCharArray()) {
            if ((Character.isWhitespace(lastChar)) && (!Character.isWhitespace(c))
                    && ('-' != c) && (buf.length() > 0)
                    && (buf.charAt(buf.length() - 1) != '-')) {
                buf.append(StringPool.DASH);
            }
            if ('_' == c) {
                buf.append('-');
            } else if ('.' == c) {
                buf.append('-');
            } else if (!Character.isWhitespace(c)) {
                buf.append(Character.toLowerCase(c));
            }
            lastChar = c;
        }
        if (Character.isWhitespace(lastChar)) {
            buf.append(StringPool.DASH);
        }
        return buf.toString();
    }

    /**
     * 从字符串中移除一个单词及随后的一个逗号
     *
     * @param s 原字符串
     * @param p 移除的单词
     * @return ignore
     * @deprecated 3.1.1
     */
    @Deprecated
    public static String removeWordWithComma(String s, String p) {
        String match = "\\s*" + p + "\\s*,{0,1}";
        return s.replaceAll(match, "");
    }
}
