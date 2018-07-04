package com.rnkrsoft.platform.client.utils;

import java.util.HashMap;
import java.util.Map;

public class MessageFormatter {
    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    public MessageFormatter() {
    }

    public static String format(String format, Object... args) {
        int i = 0;
        StringBuilder sbuf = new StringBuilder(format.length() + 50);

        for(int len = 0; len < args.length; ++len) {
            int placeholderIdx = format.indexOf("{}", i);
            if(placeholderIdx == -1) {
                if(i == 0) {
                    return format;
                }

                sbuf.append(format, i, format.length());
                return sbuf.toString();
            }

            if(isEscapedDelimeter(format, placeholderIdx)) {
                if(!isDoubleEscaped(format, placeholderIdx)) {
                    --len;
                    sbuf.append(format, i, placeholderIdx - 1);
                    sbuf.append('{');
                    i = placeholderIdx + 1;
                } else {
                    sbuf.append(format, i, placeholderIdx - 1);
                    deeplyAppendParameter(sbuf, args[len], new HashMap());
                    i = placeholderIdx + 2;
                }
            } else {
                sbuf.append(format, i, placeholderIdx);
                deeplyAppendParameter(sbuf, args[len], new HashMap());
                i = placeholderIdx + 2;
            }
        }

        sbuf.append(format, i, format.length());
        return sbuf.toString();
    }

    static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if(delimeterStartIndex == 0) {
            return false;
        } else {
            char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
            return potentialEscape == 92;
        }
    }

    static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == 92;
    }

    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
        if(o == null) {
            sbuf.append("null");
        } else {
            if(!o.getClass().isArray()) {
                safeObjectAppend(sbuf, o);
            } else if(o instanceof boolean[]) {
                booleanArrayAppend(sbuf, (boolean[])((boolean[])o));
            } else if(o instanceof byte[]) {
                byteArrayAppend(sbuf, (byte[])((byte[])o));
            } else if(o instanceof char[]) {
                charArrayAppend(sbuf, (char[])((char[])o));
            } else if(o instanceof short[]) {
                shortArrayAppend(sbuf, (short[])((short[])o));
            } else if(o instanceof int[]) {
                intArrayAppend(sbuf, (int[])((int[])o));
            } else if(o instanceof long[]) {
                longArrayAppend(sbuf, (long[])((long[])o));
            } else if(o instanceof float[]) {
                floatArrayAppend(sbuf, (float[])((float[])o));
            } else if(o instanceof double[]) {
                doubleArrayAppend(sbuf, (double[])((double[])o));
            } else {
                objectArrayAppend(sbuf, (Object[])((Object[])o), seenMap);
            }

        }
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String t = o.toString();
            sbuf.append(t);
        } catch (Throwable var3) {
            System.err.println("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]");
            var3.printStackTrace();
            sbuf.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if(!seenMap.containsKey(a)) {
            seenMap.put(a, (Object)null);
            int len = a.length;

            for(int i = 0; i < len; ++i) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if(i != len - 1) {
                    sbuf.append(", ");
                }
            }

            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }

        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        sbuf.append('[');
        int len = a.length;

        for(int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if(i != len - 1) {
                sbuf.append(", ");
            }
        }

        sbuf.append(']');
    }
}
