package com.onpositive.semantic.model.ui.roles;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class URLImageDescriptor implements ImageDescriptor,Serializable{

	protected final String url;
	
	public String getUrl() {
		return url;
	}

	public URLImageDescriptor(String url){
		this.url=url;
	}
	
	public URLImageDescriptor(URL url){
		this.url=url.toExternalForm();
	}
	
	public URL getActualUrl(){
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URLImageDescriptor other = (URLImageDescriptor) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
