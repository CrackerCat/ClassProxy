import com.bug.proxy.Proxy;
import com.bug.proxy.ProxyCallback;

public class Main
{
	public static void main(String[] args)throws Throwable
	{
        MyObject object=new MyObject("老婆");
        boolean green=true;
        if (green)
        {
            Proxy proxy=new Proxy();
            proxy.setSuperClass(MyObject.class);
            //proxy.setRawObject(object);
            /*proxy.setCallback(new ProxyCallback()
             {
             @Override
             public Object call(Object proxyObject, Method method, Object[] args)
             {
             if (method.getName().equals("getMsg"))
             {
             return "fuck♂";
             }
             return Proxy.callSuper(proxyObject, method, args);
             }
             });*/
            proxy.setCallback(new ProxyCallback.Hook()
                {
                    @Override
                    protected void beforeHookedMethod(final Param param) throws Throwable
                    {
                        if (param.method.getName().equals("getMsg"))
                        {
                            param.setObjectExtra("value", "fuck♂");
                        }
                    }
                    @Override
                    protected void afterHookedMethod(final Param param) throws Throwable
                    {
                        if (param.method.getName().equals("getMsg"))
                        {
                            param.setResult(param.getObjectExtra("value"));
                        }
                    }
                });
            object = (MyObject)proxy.create();
        }
        System.out.println(object.getMsg());
    }
}
