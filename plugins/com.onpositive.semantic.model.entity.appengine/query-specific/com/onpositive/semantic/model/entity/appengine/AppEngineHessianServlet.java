package com.onpositive.semantic.model.entity.appengine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.server.HessianServlet;

public class AppEngineHessianServlet extends HessianServlet{

	public static class ThrowableSerializer extends AbstractSerializer {
	    @Override
	    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
	        if (obj != null) {
	        	
	            final Class cl = obj.getClass();
	            if (out.addRef(obj))
	                return;
	            int ref = out.writeObjectBegin(cl.getName());
	            Throwable tr = (Throwable) obj;
	            if (cl!=null){
	            	throw new IllegalStateException(tr);
	            }
	        
	            ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
	            try {
	                oos.writeObject(tr);

	                if (ref < -1) {
	                    out.writeString("value");
	                    out.writeBytes(bos.toByteArray());
	                    out.writeMapEnd();
	                } else {
	                    if (ref == -1) {
	                        out.writeInt(1);
	                        out.writeString("value");
	                        out.writeObjectBegin(cl.getName());
	                    }
	                    out.writeBytes(bos.toByteArray());
	                }
	            } finally {
	                oos.close();
	                bos.close();
	            }
	        } else
	            out.writeNull();
	    }
	}
	public static class ThrowableDeserializer extends AbstractDeserializer {

	    @Override
	    public Class getType() {
	        return Throwable.class;
	    }

	    @Override
	    public Object readMap(AbstractHessianInput in) throws IOException {
	        int ref = in.addRef(null);
	        byte[] initValue = null;
	        while (!in.isEnd()) {
	            String key = in.readString();

	            if (key.equals("value"))
	                initValue = in.readBytes();
	            else
	                in.readString();
	        }

	        in.readMapEnd();
	        ByteArrayInputStream bis = new ByteArrayInputStream(initValue);
	        ObjectInputStream ois = new ObjectInputStream(bis);
	        try {
	            Object value = ois.readObject();
	            in.setRef(ref, value);
	            return value;
	        } catch (ClassNotFoundException e) {
	            throw new RuntimeException(e);
	        } finally {
	            ois.close();
	            bis.close();
	        }
	    }

	    @Override
	    public Object readObject(AbstractHessianInput in, Object[] fieldNames)
	            throws IOException {
	        int ref = in.addRef(null);
	        byte[] initValue = null;
	        for (Object o : fieldNames) {
	            if (o instanceof String) {
	                final String key = (String) o;
	                if (key.equals("value"))
	                    initValue = in.readBytes();
	                else
	                    in.readObject();
	            }
	        }
	        ByteArrayInputStream bis = new ByteArrayInputStream(initValue);
	        ObjectInputStream ois = new ObjectInputStream(bis);
	        try {
	            Object value = ois.readObject();
	            in.setRef(ref, value);
	            return value;
	        } catch (ClassNotFoundException e) {
	            throw new RuntimeException(e);
	        } finally {
	            ois.close();
	            bis.close();
	        }
	    }
	}
	
	public class ThrowableSerializerFactory extends AbstractSerializerFactory {
	    @Override
	    public Serializer getSerializer(Class cl) throws HessianProtocolException {
	        if (Throwable.class.isAssignableFrom(cl)) {
	            return new ThrowableSerializer();
	        }
	        return null;
	    }

	    @Override
	    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
	        if (Throwable.class.isAssignableFrom(cl)) {
	            return new ThrowableDeserializer();
	        }
	        return null;
	    }
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
        super.init(config);
        getSerializerFactory().addFactory(new ThrowableSerializerFactory());
    }
}
