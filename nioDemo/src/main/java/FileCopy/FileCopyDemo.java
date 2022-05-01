package FileCopy;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileCopyDemo {
    private static final int ROUNDS = 5;


    private static void benchmark(FileCopyRunner test,File source,File target){
        long elapsed = 0;
        for (int i = 0; i < ROUNDS; i++ ) {
            long startTime = System.currentTimeMillis();
            test.copyFile(source,target);
            elapsed += System.currentTimeMillis() - startTime;
            target.delete();
        }
        System.out.println(test + " : " + elapsed / ROUNDS);
    }
    private static void close(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        // lambda表达式 不能重载 toString方法
        FileCopyRunner noBufferStreamCopy = (source, target) -> {
            FileInputStream fin = null;
            FileOutputStream fout = null;

            try {
                fin = new FileInputStream(source);
                fout = new FileOutputStream(target);
                int result;
                while ((result = fin.read()) != -1){
                    fout.write(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(fin);
                close(fout);
            }

        };

        FileCopyRunner bufferdStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream fin = null;
                OutputStream fout = null;
                try {
                    fin = new BufferedInputStream(
                            new FileInputStream(source)
                    );
                    fout = new BufferedOutputStream(
                            new FileOutputStream(target)
                    );
                    byte[] buffer = new byte[1024];
                    int result ;
                    while ((result = fin.read(buffer)) != -1){
                        // result 在没结束前，的值时 read 一次读取的数量
                        fout.write(buffer,0,result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }

            }
            @Override
            public String toString() {
                return "bufferdStreamCopy";
            }
        };

        FileCopyRunner nioBufferCopy =(source,target) ->{

            FileChannel fin = null;
            FileChannel fout = null;
            try {
                fin = new FileInputStream(source).getChannel();
                fout = new FileOutputStream(target).getChannel();

                ByteBuffer buffer = ByteBuffer.allocate(1024);

                while ((fin.read(buffer)) != -1){
                    buffer.flip();
                    while (buffer.hasRemaining()){
                        // 确保buffer能写完
                        fout.write(buffer); // 不一定能写完
                    }
                    buffer.clear();//因为 while(buffer.hasRemaining)  保证了全部传输完。
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(fin);
                close(fout);
            }
        };

        FileCopyRunner nioTransferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;

                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    long transferred = 0L;

                    while (transferred != fin.size()){
                        transferred += fin.transferTo(0,fin.size(),fout); //不能百分百 把所有数据传输过去
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }

            }

            @Override
            public String toString() {
                return "nioTransferCopy";
            }
        };
        // 获取当前项目 resource 路径下的文件方式
        String path = FileCopyDemo.class.getClassLoader().getResource("").getPath();
        File source = new File(path+"var/source.txt");
        File target = new File(path+"var/target.txt");
        benchmark(noBufferStreamCopy,source,target);
        benchmark(bufferdStreamCopy,source,target);
        benchmark(nioBufferCopy,source,target);
        benchmark(nioTransferCopy,source,target);
    }
}
