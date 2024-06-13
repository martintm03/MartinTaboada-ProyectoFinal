package com.curso.BigStep.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService {
	private String folder="images//";
	
	// En caso de que no se añada una imagen al producto se adjuntara una imagen por defecto
	public String saveImage(MultipartFile file) throws IOException {
		if (!file.isEmpty()) {
			byte [] bytes=file.getBytes();
			Path path =Paths.get(folder+file.getOriginalFilename());
			Files.write(path, bytes);
			return file.getOriginalFilename();
		}
		return "default.jpg";
	}
	
	// Borra la imagen de la carpeta "Images"
	public void deleteImage(String nombre) {
		String ruta="images//";
		File file= new File(ruta+nombre);
		file.delete();
	}

}
