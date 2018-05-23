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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Updater.UpdateProviders.BukkitUpdateProvider;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.*;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, JavaPlugin.class, PluginDescriptionFile.class, Thread.class })
public class UpdaterTest
{
	private static final File PLUGINS_FOLDER = new File("plugins"), TARGET_FILE = new File(PLUGINS_FOLDER, "updates" + File.separator + "MM.jar");

	private static PluginDescriptionFile mockedPluginDescription;
	private static String runnableStatus = "";

	private Runnable syncRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			runnableStatus = "SYNC";
		}
	};

	private Runnable asyncRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			runnableStatus = "ASYNC";
			try
			{
				Thread.sleep(200);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	};

	@BeforeClass
	public static void prepareTestData()
	{
		File updateFolder = new File("plugins/updates");
		//noinspection ResultOfMethodCallIgnored
		updateFolder.mkdirs();
	}

	@Before
	public void prepareTestObjects() throws Exception
	{
		TestObjects.initMockedJavaPlugin();
		whenNew(at.pcgamingfreaks.Updater.Updater.class).withAnyArguments().thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock)
			{
				return null;
			}
		});
		suppress(at.pcgamingfreaks.Updater.Updater.class.getDeclaredMethods());
		mockedPluginDescription = mock(PluginDescriptionFile.class);
		when(mockedPluginDescription.getVersion()).thenReturn("v1.8.2-SNAPSHOT");
		when(mockedPluginDescription.getAuthors()).thenReturn(new ArrayList<String>());
		when(TestObjects.getJavaPlugin().getDescription()).thenReturn(mockedPluginDescription);
		mockStatic(Bukkit.class);
		when(Bukkit.getUpdateFolderFile()).thenReturn(new File("plugins/updates"));
	}

	@Test
	public void testUpdater() throws Exception
	{
		Updater updater = new Updater(TestObjects.getJavaPlugin(), TARGET_FILE, true, new BukkitUpdateProvider(74734, TestObjects.getJavaPlugin().getLogger()));
		assertNotNull("The updater should not be null", updater);
		updater.runSync(syncRunnable);
		assertEquals("The runnable status text should match", "SYNC", runnableStatus);
		updater.runAsync(asyncRunnable);
		updater.waitForAsyncOperation();
		assertEquals("The runnable status text should match", "ASYNC", runnableStatus);
		Field thread = Updater.class.getDeclaredField("thread");
		thread.setAccessible(true);
		thread.set(updater, null);
		Thread mockedThread = mock(Thread.class);
		whenNew(Thread.class).withAnyArguments().thenReturn(mockedThread);
		updater.runAsync(asyncRunnable);
		updater.waitForAsyncOperation();
		assertEquals("No author should be found", "", updater.getAuthor());
		when(mockedPluginDescription.getAuthors()).thenAnswer(new Answer<List<String>>()
		{
			@Override
			public List<String> answer(InvocationOnMock invocationOnMock)
			{
				List<String> authorList = new ArrayList<>();
				authorList.add("MarkusWME");
				authorList.add("GeorgH93");
				return authorList;
			}
		});
		assertEquals("The author should match", "MarkusWME", updater.getAuthor());
		updater.waitForAsyncOperation();
		thread.set(updater, null);
		updater.waitForAsyncOperation();
		thread.setAccessible(false);
	}

	public static int TEST_TIMEOUT = 3000;

	@SuppressWarnings("deprecation")
	@Rule
	public Timeout timeout = new Timeout(TEST_TIMEOUT)
	{
		public Statement apply(Statement base, Description description)
		{
			return new FailOnTimeout(base, TEST_TIMEOUT)
			{
				@Override
				public void evaluate() throws Throwable
				{
					try
					{
						super.evaluate();
						throw new TimeoutException();
					}
					catch(Exception ignored)
					{
					}
				}
			};
		}
	};

	@Test
	public void testWaitForAsync() throws Exception
	{
		TestUtils.initReflection();
		Updater updater = new Updater(TestObjects.getJavaPlugin(), TARGET_FILE, true, new BukkitUpdateProvider(74734, TestObjects.getJavaPlugin().getLogger()));
		final Thread mockedThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				int i = 0;
				//noinspection InfiniteLoopStatement
				while(true)
				{
					i++;
					if(i > 1000000000)
					{
						i = -1000000000;
					}
				}
			}
		});
		Thread starterThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				mockedThread.start();
				try
				{
					Thread.sleep(1000);
					mockedThread.interrupt();
					Thread.currentThread().interrupt();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});
		Field thread = TestUtils.setAccessible(Updater.class, updater, "thread", mockedThread);
		starterThread.start();
		Thread.sleep(100);
		updater.waitForAsyncOperation();
		TestUtils.setUnaccessible(thread, updater, false);
	}

	@AfterClass
	public static void cleanupTestData()
	{
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updates").delete();
		//noinspection ResultOfMethodCallIgnored
		new File("plugins").delete();
	}
}