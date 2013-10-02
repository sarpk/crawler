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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class LocalDataCollectorCrawler extends WebCrawler {

	//String downloadFolder = "C:/crawl/download/";
	Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	CrawlStat myCrawlStat;
	String downloadFolder = Addresses.fileLocation + "/download/";
	public LocalDataCollectorCrawler() {
		myCrawlStat = new CrawlStat();
	}
	String checkWebAddress = Addresses.webAddressToBeChecked;
	
	
	
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !filters.matcher(href).matches() && href.contains(checkWebAddress);
	}

	@Override
	public void visit(Page page) {
		//System.out.println("Visited: " + page.getWebURL().getURL());
		myCrawlStat.incProcessedPages();

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData parseData = (HtmlParseData) page.getParseData();
			List<WebURL> links = parseData.getOutgoingUrls();
			myCrawlStat.incTotalLinks(links.size());
			
			String content = parseData.getText();
			URL addressURL = null;
			try {
				addressURL = new URL(page.getWebURL().toString());
			} catch (MalformedURLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			String urlSubs[] = addressURL.getFile().toString().split("/");
			String urlNotEncoded = addressURL.getHost();
			for (int i = 0; i < urlSubs.length-1; i++) {
				urlNotEncoded += urlSubs[i] + "/";
			}
			
			String pathEncoded = urlSubs[urlSubs.length-1];
			try {
				pathEncoded = URLEncoder.encode(urlSubs[urlSubs.length-1], "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String downloadPath = downloadFolder + urlNotEncoded + pathEncoded + ".txt";
			
			File f = new File(downloadPath);
			f.getParentFile().mkdirs();
			
			try {

				PrintWriter out = new PrintWriter(downloadPath);
				out.println(content);
				out.close();
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Downloaded path is " + downloadPath);
			System.out.println("URL orig is " + page.getWebURL());
			/*String searchIt = "Engineering";
			if (content.toLowerCase().contains(searchIt.toLowerCase())) {
				System.out.println(searchIt + " is found on " + page.getWebURL().getURL() );
			}*/
			try {
				myCrawlStat.incTotalTextSize(parseData.getText().getBytes("UTF-8").length);
			} catch (UnsupportedEncodingException ignored) {
				// Do nothing
			}
		}
		// We dump this crawler statistics after processing every 50 pages
		if (myCrawlStat.getTotalProcessedPages() % 50 == 0) {
			dumpMyData();
		}
	}

	// This function is called by controller to get the local data of this
	// crawler when job is finished
	@Override
	public Object getMyLocalData() {
		return myCrawlStat;
	}

	// This function is called by controller before finishing the job.
	// You can put whatever stuff you need here.
	@Override
	public void onBeforeExit() {
		dumpMyData();
	}

	public void dumpMyData() {
		int id = getMyId();
		// This is just an example. Therefore I print on screen. You may
		// probably want to write in a text file.
		System.out.println("Crawler " + id + "> Processed Pages: " + myCrawlStat.getTotalProcessedPages());
		System.out.println("Crawler " + id + "> Total Links Found: " + myCrawlStat.getTotalLinks());
		System.out.println("Crawler " + id + "> Total Text Size: " + myCrawlStat.getTotalTextSize());
	}
}
