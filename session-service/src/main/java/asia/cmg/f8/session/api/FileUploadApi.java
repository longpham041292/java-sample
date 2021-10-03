package asia.cmg.f8.session.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.session.service.FileUploadService;









@RestController
public class FileUploadApi {

	
	 private static final Logger LOG = LoggerFactory.getLogger(FileUploadApi.class);
	 
	 @Autowired 
	 private FileUploadService fileUploadService;
	 
	
	@RequestMapping(value="/admin/trainers/upload", method=RequestMethod.POST)
    @ResponseBody
    @RequiredAdminRole
    public ResponseEntity upload(@RequestParam("upfile") final MultipartFile upfile) throws IOException
    {

		LOG.info("-- # file upload # --");

		return new ResponseEntity<>(fileUploadService.uploadData(upfile), HttpStatus.OK);
    
    }	
}
