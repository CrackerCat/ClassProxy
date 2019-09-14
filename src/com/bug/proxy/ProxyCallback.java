package com.bug.proxy;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

public interface ProxyCallback
{
    public Object call(Object proxyObject, Method method, Object[] args) throws Throwable;

    public static abstract class Hook implements ProxyCallback
    {
        @Override
        public Object call(Object proxyObject, Method method, Object[] args) throws Throwable
        {
            ProxyCallback.Param param = new Param();
            param.method = method;
            param.thisObject = proxyObject;
            param.args = args;
            try
            {
                beforeHookedMethod(param);
            }
            catch (final Throwable t)
            {
                param.setResult(null);
                param.returnEarly = false;
            }
            if (!param.returnEarly)
            {
                try
                {
                    param.setResult(Proxy.callSuper(param.thisObject, param.method, param.args));
                }
                catch (final Throwable t)
                {
                    param.setThrowable(t);
                }
            }
            final Object lastResult = param.getResult();
            final Throwable lastThrowable = param.getThrowable();
            try
            {
                afterHookedMethod(param);
            }
            catch (final Throwable t)
            {
                if (lastThrowable == null)
                    param.setResult(lastResult);
                else
                    param.setThrowable(lastThrowable);
            }
            if (param.hasThrowable())
                throw param.getThrowable();
            else
                return param.getResult();
        }

        protected void beforeHookedMethod(final Param param) throws Throwable
        {}

        protected void afterHookedMethod(final Param param) throws Throwable
        {}
    }
    public static final class Param
    {
        public Method method;
        public Object thisObject;
        public Object[] args;
        private Object result = null;
        public LinkedHashMap<String,Object> extra;
        private Throwable throwable = null;
        public boolean returnEarly = false;

        public Object getResult()
        {
            return result;
        }

        public void setResult(final Object result)
        {
            this.result = result;
            this.throwable = null;
            this.returnEarly = true;
        }

        public Throwable getThrowable()
        {
            return throwable;
        }

        public boolean hasThrowable()
        {
            return throwable != null;
        }

        public void setThrowable(final Throwable throwable)
        {
            this.throwable = throwable;
            this.result = null;
            this.returnEarly = true;
        }

        public Object getResultOrThrowable() throws Throwable
        {
            if (throwable != null)
                throw throwable;
            return result;
        }

        public synchronized LinkedHashMap<String,Object> getExtra()
        {
            if (extra == null)
                extra = new LinkedHashMap<String,Object>();
            return extra;
        }

        public Object getObjectExtra(String key)
        {
            LinkedHashMap<String, Object> extra = getExtra();
            if (extra.containsKey(key))
                return extra.get(key);
            return null;
        }
        
        public void setObjectExtra(String key, Object o)
        {
            getExtra().put(key, o);
        }
    }
}
