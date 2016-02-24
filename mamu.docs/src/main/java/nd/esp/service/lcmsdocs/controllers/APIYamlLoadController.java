package nd.esp.service.lcmsdocs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import javax.servlet.ServletContext;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;


@Controller
@RequestMapping("/api/yaml")
public class APIYamlLoadController {

	private static final String apiYamlHome = "WEB-INF/api_designer/api-docs";

	private static final int BUFFER_SIZE = 1024;
	
	@RequestMapping(value = "/{fileName}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE })
	@ResponseBody
	public String loadYamlFile(@PathVariable String fileName) {
		WebApplicationContext webApplicationContext = org.springframework.web.context.ContextLoader
				.getCurrentWebApplicationContext();
		ServletContext servletContext = webApplicationContext.getServletContext();
		return this.readByNIO(new StringBuffer().append(servletContext.getRealPath("")).append(File.separator)
				.append(apiYamlHome).append(File.separator).append(fileName).append(".yaml").toString());
	}
	
	@RequestMapping(value = "/homeList", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE})
	@ResponseBody
	public String loadAPIFileList() {
		WebApplicationContext webApplicationContext = org.springframework.web.context.ContextLoader
				.getCurrentWebApplicationContext();
		ServletContext servletContext = webApplicationContext.getServletContext();
		File homeDir = new File(new StringBuffer().append(servletContext.getRealPath("")).append(File.separator)
				.append(apiYamlHome).toString());
		String[] fileList = homeDir.list();
		StringBuffer strNameDiv = new StringBuffer();
		String divString = "<div class=\"voice {}\"><a class=\"label\" href=\"api/yaml/fileName\">fileName</a></div>";
		for (String name : fileList) {
			
			strNameDiv.append(divString.replaceAll("fileName", name)).append("<br>");
		}
		return strNameDiv.toString();
	}

	@SuppressWarnings({ "finally", "resource" })
	private String readByNIO(String file) {
		Charset charset = Charset.forName("UTF-8");//Java.nio.charset.Charset处理了字符转换问题。它通过构造CharsetEncoder和CharsetDecoder将字符序列转换成字节和逆转换。  
        CharsetDecoder decoder = charset.newDecoder();  
		StringBuilder content = new StringBuilder();
		try {
			RandomAccessFile aFile = new RandomAccessFile(file, "r");
			FileChannel inChannel = aFile.getChannel();
	        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
	        CharBuffer charBuffer = CharBuffer.allocate((int)inChannel.size());
	        buffer.load(); 
	        decoder.decode(buffer, charBuffer, false);  
            charBuffer.flip();  
	        for (int i = 0; i < charBuffer.limit(); i++)
	        {
	           content.append(charBuffer.get());
	        }
	        buffer.clear(); // do something with the data and clear/compact it.
	        charBuffer.clear();
	        inChannel.close();
	        aFile.close();

		} catch (FileNotFoundException e) {
			return "no this file";
		} catch (IOException e) {
			return "file read error";
		}

		return content.toString();
	}
}
