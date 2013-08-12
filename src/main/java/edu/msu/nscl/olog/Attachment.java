/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.io.InputStream;

/**
 *
 * @author berryman
 */
public class Attachment {
    
  public Attachment() {
        }
        
        private InputStream content;
        private String mimeType;
        private String encoding;
        private String fileName;
        private Long fileSize;
        
        public void setContent(InputStream content){
            this.content = content;
        }
        
        public InputStream getContent(){
            return content;
        }
        
        public void setMimeType(String mimeType){
            this.mimeType = mimeType;
        }
        
        public String getMimeType(){
            return mimeType;
        }
        
        public void setFileName(String fileName){
            this.fileName = fileName;
        }
        
        public String getFileName(){
            return fileName;
        }
        
        public void setFileSize(Long fileSize){
            this.fileSize = fileSize;
        }
        
        public Long getFileSize(){
            return fileSize;
        }
        
        public void setEncoding(String encoding){
            this.encoding = encoding;
        }
        
        public String getEncoding(){
            return encoding;
        }  
}
