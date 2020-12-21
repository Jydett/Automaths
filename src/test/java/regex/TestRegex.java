package regex;

import fr.iutvalence.automath.app.bridge.BasicAutomatonOperator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;

public class TestRegex {


    private static Method clean;
    private static BasicAutomatonOperator basicAutomatonOperator;

    @BeforeClass
    public static void init() {
        basicAutomatonOperator = new BasicAutomatonOperator();
        try {
            clean = BasicAutomatonOperator.class.getDeclaredMethod("clean", String.class);
            clean.setAccessible(true);
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

    public String clean(String arg) {
        try {
            return (String) clean.invoke(basicAutomatonOperator, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            fail();
            return null;
        }
    }

    @Test
    public void testClean() {
        Assert.assertEquals("a", clean("(a)"));
        Assert.assertEquals("ab", clean("(ab)"));
        Assert.assertEquals("ab", clean("ab"));
        Assert.assertEquals("aa*", clean("()()()a()a*"));
        Assert.assertEquals("aa*", clean("(a())a*"));
        Assert.assertEquals("(a*)a*", clean("(a*())a*"));
        Assert.assertEquals("a", clean(".{0}a"));
        Assert.assertEquals("a", clean("(.{0})a"));
        Assert.assertEquals("aaaaa", clean("(aaaaa)"));
        Assert.assertEquals("(aaa)aa", clean("((aaa)aa)"));
        Assert.assertEquals("(aaa)(aa)", clean("((aaa)(aa))"));
        Assert.assertEquals("(aaa)(aa)", clean("(aaa)(aa)"));
        Assert.assertEquals("(aaa)(aa)", clean("(aaa)((aa))"));
        Assert.assertEquals("(aaa)(aa)", clean("(aaa)((aa)|.{0})"));
    }
}
