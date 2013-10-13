/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package crawl.cr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class LocalDataCollectorController {

	public static void main(String[] args) throws Exception {
		/*
		 * if (args.length != 2) { System.out.println("Needed parameters: ");
		 * System
		 * .out.println("\t rootFolder (it will contain intermediate crawl data)"
		 * );
		 * System.out.println("\t numberOfCralwers (number of concurrent threads)"
		 * ); return; }
		 */
		String rootFolder = "C:/crawl3/another";
		String webAddress = "http://www.student.qut.edu.au/studying";
		String checkWebAddress = "";
		String robotName = "FunnelBack";
		boolean robotsEnabled = true;
		int numberOfCrawlers = 10;
		int politenessDelay = 1000;
		int amountOfPages = -1;
		int depthOfPages = -1;

		String usage = "WebCrawler"
				+ " [-site http address] [-baseaddr http address] [-folder ROOT_FOLDER]"
				+ " [-crawlers CRAWLER_AMOUNT] [-robots true/false] [-robotname useragentname]"
				+ " [-politeness DELAY_MS] [-depth CRAWL_DEPTH] [-amount PAGE_AMOUNT]\n\n";
		for (int i = 0; i < args.length; i++) {
			if ("-site".equals(args[i])) {
				webAddress = args[i + 1];
				i++;
			} else if ("-folder".equals(args[i])) {
				rootFolder = args[i + 1];
				i++;
			} else if ("-crawlers".equals(args[i])) {
				numberOfCrawlers = Integer.valueOf(args[i + 1]);
			} else if ("-baseaddr".equals(args[i])) {
				checkWebAddress = args[i + 1];
			} else if ("-robots".equals(args[i])) {
				if (args[i + 1].equals("false")) {
					robotsEnabled = false;
				}
			} else if ("-robotname".equals(args[i])) {
				robotName = args[i + 1];
			} else if ("-politeness".equals(args[i])) {
				politenessDelay = Integer.valueOf(args[i + 1]);
			} else if ("-depth".equals(args[i])) {
				depthOfPages = Integer.valueOf(args[i + 1]);
			} else if ("-amount".equals(args[i])) {
				amountOfPages = Integer.valueOf(args[i + 1]);
			}
		}
		if (rootFolder == null) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		if (checkWebAddress.equals("")) {
			try {
				URL checkURL = new URL(webAddress);
				checkWebAddress = checkURL.getHost() + checkURL.getFile();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Addresses.webAddressToBeChecked = checkWebAddress;
		Addresses.fileLocation = rootFolder;

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(rootFolder);
		System.out.println(config.getMaxPagesToFetch());
		config.setMaxPagesToFetch(amountOfPages);
		config.setMaxDepthOfCrawling(depthOfPages);
		config.setPolitenessDelay(politenessDelay);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setUserAgentName(robotName);
		robotstxtConfig.setEnabled(robotsEnabled);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher,
				robotstxtServer);

		controller.addSeed(webAddress);

		controller.start(LocalDataCollectorCrawler.class, numberOfCrawlers);

		List<Object> crawlersLocalData = controller.getCrawlersLocalData();
		long totalLinks = 0;
		long totalTextSize = 0;
		int totalProcessedPages = 0;
		for (Object localData : crawlersLocalData) {
			CrawlStat stat = (CrawlStat) localData;
			totalLinks += stat.getTotalLinks();
			totalTextSize += stat.getTotalTextSize();
			totalProcessedPages += stat.getTotalProcessedPages();
		}
		System.out.println("Aggregated Statistics:");
		System.out.println("   Processed Pages: " + totalProcessedPages);
		System.out.println("   Total Links found: " + totalLinks);
		System.out.println("   Total Text Size: " + totalTextSize);
	}

}
