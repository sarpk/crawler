package crawl.cr;

import java.net.MalformedURLException;
import java.net.URL;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CollectorClass implements Runnable {
	private String rootFolder = null;
	private String webAddress = null;
	private String checkWebAddress = "";
	private String robotName = "FunnelBack";
	private boolean robotsEnabled = true;
	private int numberOfCrawlers = 10;
	private int politenessDelay = 1000;
	private int amountOfPages = -1;
	private int depthOfPages = -1;
	private boolean crawlingFinished = false;
	
	public CollectorClass(String wAddress, String rFolder) {
		// TODO Auto-generated constructor stub
		this.webAddress = wAddress;
		this.rootFolder = rFolder;
	}

	public void setDepth(int i) {
		depthOfPages = i;
		
	}

	public void setMaxFetch(int i) {
		amountOfPages = i;
		
	}

	public void setRobots(boolean b) {
		robotsEnabled = b;
		
	}

	public void setPoliteness(int i) {
		politenessDelay = i;
	}

	public void setCrawlerAmount(int i) {
		numberOfCrawlers = i;
		
	}
	
	public void startCrawling() {
		 (new Thread(this)).start();
	}

	public boolean isCrawlingFinished() {
		return crawlingFinished;
	}

	public void run() {
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
		config.setMaxPagesToFetch(amountOfPages);
		config.setMaxDepthOfCrawling(depthOfPages);
		config.setPolitenessDelay(politenessDelay);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setUserAgentName(robotName);
		robotstxtConfig.setEnabled(robotsEnabled);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		CrawlController controller = null;
		try {
			controller = new CrawlController(config, pageFetcher,
					robotstxtServer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		controller.addSeed(webAddress);

		controller.start(LocalDataCollectorCrawler.class, numberOfCrawlers);
		
		System.out.println("crawling finished");
		crawlingFinished = true;
		
	}

}
