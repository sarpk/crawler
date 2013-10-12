package crawl.cr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.uci.ics.crawler4j.url.WebURL;

public class FileCreator {
	private String downloadP = null;
	private WebURL pageURL = null;
	private String dContent = null;
	private static int RAND_AMOUNT = 16;
	private static String SPLITTER = " ; ";
	private static String FILEEXT = ".txt";
	private static ThreadBlock block = null;

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public FileCreator(String downloadFolder, String content, WebURL webURL) {
		// TODO Auto-generated constructor stub
		URL addressURL = null;
		try {
			addressURL = new URL(webURL.toString());
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		pageURL = webURL;
		dContent = content;
		String urlSubs[] = addressURL.getFile().toString().split("/");
		String urlNotEncoded = addressURL.getHost();
		for (int i = 0; i < urlSubs.length - 1; i++) {
			urlNotEncoded += urlSubs[i] + "/";
		}

		/*
		 * String pathEncoded = urlSubs[urlSubs.length - 1]; try { pathEncoded =
		 * URLEncoder.encode(urlSubs[urlSubs.length - 1], "UTF-8"); } catch
		 * (UnsupportedEncodingException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); } String downloadPath = downloadFolder +
		 * urlNotEncoded + pathEncoded + TXTEXT;
		 * 
		 * File f = new File(downloadPath); f.getParentFile().mkdirs();
		 */
		downloadP = downloadFolder + urlNotEncoded;
		File f = new File(downloadP + "a");
		f.getParentFile().mkdirs();// create all subfolders
		block = ThreadBlock.getInstance();
	}

	public void writeToFile() {
			String alphaNumerics = "qwertyuiopasdfghjklzxcvbnm1234567890";
			String t = "";
			for (int i = 0; i < RAND_AMOUNT; i++) {
				t += alphaNumerics.charAt((int) (Math.random() * alphaNumerics
						.length()));
			}
			t += FILEEXT;
			String dPath = downloadP + t;
			synchronized (block) {
			File f = new File(dPath);
			while (f.exists()) {
				System.out.println("Exists " + dPath);
				t = "";
				for (int i = 0; i < RAND_AMOUNT; i++) {
					t += alphaNumerics
							.charAt((int) (Math.random() * alphaNumerics
									.length()));
				}
				t += FILEEXT;
				dPath = downloadP + t;
				f = new File(dPath);
			}

			try {
				// write index so that we can know which file has which URL
				String indexPath = downloadP + "index.dat";
				String content = "";
				File indexExist = new File(indexPath);
				if (indexExist.exists()) {
					content = readFile(indexPath, Charset.defaultCharset());
				}
				int indexFound = content.indexOf(pageURL.toString());

				if (indexFound == -1) {
					PrintWriter indexW = new PrintWriter(indexPath);
					indexW.print(content);
					indexW.println(t + SPLITTER + pageURL);
					indexW.close();
				} else {// If file exists
					System.out.println("Else here " + pageURL);
					int firstSubL = indexFound - SPLITTER.length()
							- FILEEXT.length() - RAND_AMOUNT;
					dPath = downloadP
							+ content.substring(firstSubL, indexFound
									- SPLITTER.length());// change the file name
															// so that it would
															// be overwritten
					System.out.println("end Else");
				}

				// write the content
				PrintWriter webContent = new PrintWriter(dPath);
				webContent.println(dContent);
				webContent.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(dPath);
			System.out.println(pageURL);
		}
	}

}
