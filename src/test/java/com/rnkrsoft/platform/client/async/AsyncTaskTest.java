package com.rnkrsoft.platform.client.async;

import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/21.
 */
public class AsyncTaskTest {

    @Test
    public void testExecute() throws Exception {
        for (int i = 0; i < 100; i++) {
            AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    System.out.println("-----------" + Thread.currentThread().getName());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cancel(true);
                    return "this is " + params[0];
                }

                @Override
                protected void onPostExecute(String s) {
                    System.out.println(s);
                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();
                    System.out.println("取消");
                }
            };
            asyncTask.execute("test" + i);

        }
        System.out.println("-----------" + Thread.currentThread().getName());
        System.out.println("-----------" + Thread.currentThread().getName());
        Thread.sleep(10000);
    }
}