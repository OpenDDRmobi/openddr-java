/*
 * Copyright (c) 2011-2016 OpenDDR LLC and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mobi.openddr.classifier.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.lang3.time.StopWatch;
import mobi.openddr.classifier.Classifier;
import mobi.openddr.classifier.ClassifierBuilder;
import mobi.openddr.classifier.loader.LoaderOption;
import mobi.openddr.classifier.loader.impl.DDRLoader;
import mobi.openddr.classifier.model.Device;

/**
 * @author Werner Keil
 * @author Reza Naghibi
 * @version 1.3
 */
public class ClassifierConsole {
	private static final String APP_NAME = "OpenDDR Classifier Console";
	private static final String CLASS_NAME = ClassifierConsole.class.getName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);
	private static final String CONFIG_FILE = "app.properties";
	private static final String CONFIG_PROP_CONNECT = "connectionString";

	public static void main(String[] args) throws Exception {
		System.out.println(APP_NAME + " " + ClassifierConsole.class.getPackage().getImplementationVersion());

		final Configurations configs = new Configurations();
		// Read data from this file
		final File propertiesFile = new File(CONFIG_FILE);
		final PropertiesConfiguration config = configs.properties(propertiesFile);

		@SuppressWarnings("unused")
		boolean debug = false;
		String loaderPath = null;
		LoaderOption option = LoaderOption.UNINITIALIZED;
		String parameter = null;
		Level debugLevel = Level.OFF;

		CommandLine lvCmd = null;
		final HelpFormatter lvFormater = new HelpFormatter();
		final CommandLineParser lvParser = new DefaultParser();
		final Options lvOptions = new Options();

		final Option lvHelp = new Option("h", "help", false, "Show Help.");
		final Option lverbose = new Option("v", "verbose", false, "Verbose mode.");

		lvOptions.addOption(lvHelp);
		lvOptions.addOption(lverbose);
		lvOptions.addOption(new Option("o", "json", false, "Output as JSON."));

		lvOptions.addOption(Option.builder("d").longOpt("device").argName("UA").desc("User Agent of device to test")
				.hasArg().optionalArg(true).build());

		lvOptions.addOption(Option.builder("u").longOpt("url").argName("url")
				.desc("Load OpenDDR resouces from URL or \"default\"").hasArg().optionalArg(true).build());

		lvOptions.addOption(Option.builder("f").longOpt("folder").argName("path")
				.desc("Load OpenDDR resouces from folder or \"default\"").hasArg().optionalArg(true).build());

		lvOptions.addOption(Option.builder("j").longOpt("jar").argName("jar")
				.desc("Load OpenDDR resouces from JAR file in classpath or \"default\"").hasArg().optionalArg(true)
				.build());

		try {
			lvCmd = lvParser.parse(lvOptions, args);

			if (lvCmd.hasOption('h')) {
				lvFormater.printHelp(CLASS_NAME, lvOptions);
				return;
			}

			if (lvCmd.hasOption('v')) {
				debug = true;
				debugLevel = Level.ALL;
				LOG.setLevel(debugLevel);
				Logger.getLogger(Classifier.class.getName()).setLevel(debugLevel);
				Logger.getLogger(DDRLoader.class.getName()).setLevel(debugLevel);
				for (Handler h : Logger.getLogger(Classifier.class.getName()).getParent().getHandlers()) {
					if (h instanceof ConsoleHandler) {
						h.setLevel(debugLevel);
					}
				}
			}

			if (lvCmd.hasOption('f')) {
				option = LoaderOption.FOLDER;
				loaderPath = lvCmd.getOptionValue('f');
			}

			if (lvCmd.hasOption('j')) {
				LOG.fine("JAR: " + lvCmd.getOptionObject('j'));
				option = LoaderOption.JAR;
			}

			if (lvCmd.hasOption('u')) {
				option = LoaderOption.URL;
				loaderPath = lvCmd.getOptionValue('u');
				LOG.fine("URL: " + lvCmd.getOptionObject('u'));
			}

			if (lvCmd.hasOption('d')) {
				parameter = lvCmd.getOptionValue('d');
				LOG.fine("UA: " + lvCmd.getOptionObject('d'));
			}

			if ("default".equals(loaderPath)) {
				loaderPath = config.getString(CONFIG_PROP_CONNECT);
			}

			if (LoaderOption.UNINITIALIZED.equals(option)) {
				option = LoaderOption.URL;
			}
			if (loaderPath == null) {
				System.err.println("No loader URL or path given.");
				System.out.println("Try calling with '-h' or '--help' for a list of options.");
				return;
			}
			final Classifier client = new ClassifierBuilder().with(option, loaderPath).build();
			final long start = System.currentTimeMillis();
			long diff = System.currentTimeMillis() - start;

			System.out.println("Loaded " + client.getDeviceCount() + " devices with " + client.getPatternCount()
					+ " patterns and " + client.getNodeCount() + " nodes in " + diff + " ms");

			System.out.println("Cold run");
			long startn = System.nanoTime();
			final long startm = System.currentTimeMillis();
			map(client,
					"Mozilla/5.0 (Linux; U; Android 2.2; en; HTC Aria A6380 Build/ERE27) AppleWebKit/540.13+ (KHTML, like Gecko) Version/3.1 Mobile Safari/524.15.0");
			map(client,
					"Mozilla/5.0 (iPad; U; CPU OS 4_3_5 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Mobile/8L1");
			map(client,
					"Mozilla/5.0 (BlackBerry; U; BlackBerry 9810; en-US) AppleWebKit/534.11+ (KHTML, like Gecko) Version/7.0.0.261 Mobile Safari/534.11+");
			map(client,
					"Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X; en-us) AppleWebKit/536.26 (KHTML, like Gecko) CriOS/23.0.1271.91 Mobile/10A403 Safari/8536.25");
			// long diffn = (System.nanoTime() - startn) / 1000;
			final long diffm = System.currentTimeMillis() - startm;
			System.out.println("End cold run : " + diffm + " ms");

			long diffn;
			if (parameter == null) {
			} else if ((new File(parameter)).exists()) {
				System.out.println("Text file: " + parameter);
				int count = 0;
				int total = 0;

				BufferedReader in = new BufferedReader(new FileReader(parameter));
				String line;
				Device device;
				while ((line = in.readLine()) != null) {
					System.out.println("Text: '" + line + "'");
					startn = System.nanoTime();
					device = client.classifyDevice(line);
					diffn = System.nanoTime() - startn;
					total += diffn;
					count++;

					System.out.println(
							"Text lookup " + count + ": '" + device.getId() + "' time: " + (diffn / 1000) + "usec");
				}

				in.close();

				if (count == 0) {
					count = 1;
				}
				total /= count;

				System.out.println("TOTAL lookups: " + count + ", average time: " + (total / 1000) + "usec");
			} else {
				System.out.println("UA: '" + parameter + "'");
				startn = System.nanoTime();
				final Device device = client.classifyDevice(parameter);
				diffn = System.nanoTime() - startn;
				System.out.println("UA lookup: '" + device.getId() + "' time: " + (diffn / 1000) + "usec");
				if (lvCmd.hasOption('o')) {
					System.out.println("OpenDDR JSON => " + device.toString());
				} else {
					Map<String, String> m = device.getAttributes();
					// iterate thru the attributes
					if (m != null && (m.keySet() != null && m.keySet().size() > 0)) {
						System.out.println("=== ATTRIBUTES ===");
						for (String attr : m.keySet()) {
							System.out.println(attr + ": " + m.get(attr));
						}
					} else {
						System.out.println("No attributes found for '" + parameter + "'.");
					}
				}
			}

			return;
		} catch (ParseException pvException) {
			lvFormater.printHelp(CLASS_NAME, lvOptions);
			System.out.println("Parse Error:" + pvException.getMessage());
			return;
		}
	}

	private static void map(Classifier client, String text) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Device device = client.classifyDevice(text);
		stopWatch.stop();
		String deviceId = "unknown";
		if (device != null) {
			deviceId = device.getId();
		}
		System.out.println("Result: " + deviceId + " took " + stopWatch.getTime() + " ms");
	}
}
