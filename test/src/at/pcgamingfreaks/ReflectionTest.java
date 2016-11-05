/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class ReflectionTest
{
	private enum TestEnum
	{
		One,
		Two
	}

	private static class TestClass
	{
		public static String staticString = "STATIC";

		protected String value = "Constant";
		private final String CONSTANT = "CONST";

		public TestClass() {}

		@SuppressWarnings("unused")
		public TestClass(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}

		public String getConstant() { return CONSTANT; }

		@SuppressWarnings("unused")
		public String getTestValue(String value) { return value; }
	}

	private static class TestDerivedClass extends TestClass {}

	@BeforeClass
	public static void prepareTestData()
	{
		new Reflection();
	}

	@Test
	public void testClassListEqual()
	{
		assertFalse("The class list should not equal", Reflection.classListEqual(new Class<?>[] { String.class, Integer.class }, new Class<?>[] { String.class }));
		assertFalse("The class list should not equal", Reflection.classListEqual(new Class<?>[] { String.class, Integer.class }, new Class<?>[] { String.class, Double.class }));
		assertTrue("The class list should be equal", Reflection.classListEqual(new Class<?>[] { String.class, Integer.class }, new Class<?>[] { String.class, Integer.class }));
	}

	@Test
	public void testGetConstructor()
	{
		assertNotNull("The constructor should be found", Reflection.getConstructor(TestClass.class, String.class));
		assertNull("The constructor of an enum should not be found", Reflection.getConstructor(TestEnum.class));
	}

	@Test
	public void testGetEnum()
	{
		//noinspection SpellCheckingInspection
		assertEquals("The first enumeration should match TestEnum.Two", TestEnum.Two, Reflection.getEnum("at.pcgamingfreaks.ReflectionTest$TestEnum.Two"));
		//noinspection SpellCheckingInspection
		assertNull("A non existent class should return null", Reflection.getEnum("at.pcgamingfreaks.ReflectTest$TestEnum.Two"));
		assertEquals("The first enumeration should match TestEnum.One", TestEnum.One, Reflection.getEnum(TestEnum.class, "One"));
		assertNull("A invalid class should return null", Reflection.getEnum(TestClass.class, "One"));
		//noinspection SpellCheckingInspection
		assertNull("A invalid class string should return null", Reflection.getEnum("null"));
	}

	@Test
	public void testGetField()
	{
		//noinspection ConstantConditions
		assertEquals("The field name should be correct", "value", Reflection.getField(TestClass.class, "value").getName());
		assertNull("A invalid class should return null", Reflection.getField(TestEnum.class, "value"));
		//noinspection ConstantConditions
		assertEquals("The field name including parents should be correct", "value", Reflection.getFieldIncludeParents(TestClass.class, "value").getName());
		//noinspection ConstantConditions
		assertEquals("The field name including parents should be found in the parent class", "value", Reflection.getFieldIncludeParents(TestDerivedClass.class, "value").getName());
		assertNull("A null name should return null", Reflection.getFieldIncludeParents(TestDerivedClass.class, null));
		assertNull("A object should return null", Reflection.getFieldIncludeParents(Object.class, "value"));
	}

	@Test
	public void testGetMethod()
	{
		//noinspection ConstantConditions
		assertEquals("The method name should be correct", "getValue", Reflection.getMethod(TestClass.class, "getValue").getName());
		//noinspection ConstantConditions
		assertEquals("The method name should be correct", "getTestValue", Reflection.getMethod(TestClass.class, "getTestValue", String.class).getName());
		//noinspection ConstantConditions
		assertNull("A invalid class list should return null", Reflection.getMethod(TestClass.class, "getTestValue", Integer.class));
		//noinspection ConstantConditions
		assertNull("A non existent method should return null", Reflection.getMethod(TestClass.class, "getNull"));
	}

	@Test
	public void testSetValue() throws Exception
	{
		TestClass object = new TestClass();
		Reflection.setValue(object, "value", "New value");
		assertEquals("The new value should match", "New value", object.getValue());
		Reflection.setValue(object, "CONSTANT", "New value");
		assertEquals("The new value should not match the wished one", "CONST", object.getConstant());
	}

	@Test
	public void testSetStaticField()
	{
		Reflection.setStaticField(TestClass.class, "staticString", "NEW");
		assertEquals("The new value should not match", "NEW", TestClass.staticString);
		Reflection.setStaticField(TestClass.class, "finalVar", "NEW");
	}

	@Test
	public void testGetMethodIncludeParents() throws InvocationTargetException, IllegalAccessException
	{
		assertEquals("The method should be executable", "CONST", Reflection.getMethodIncludeParents(TestDerivedClass.class, "getConstant").invoke(new TestDerivedClass()));
		assertNull("The method should not be found", Reflection.getMethodIncludeParents(TestDerivedClass.class, "abc"));
		assertNull("An exception should be thrown", Reflection.getMethodIncludeParents(null, "abc"));
	}
}