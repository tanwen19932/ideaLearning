package news;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import hbase.util.HbaseUtil;
import hbase.util.RowkeyUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings("deprecation")
public class NewsDaoTest
        implements INewsDao {
    protected static Log LOG = LogFactory.getLog(NewsDaoTest.class);
    static final String table_log_name = "NewsArticleTest";
    protected static final int tableN = 2;
    static final int threadN = 2;

    static boolean isWrite = true;

    protected static HTable[] wTableLog;
    protected static Random random = new Random();


    static {
        wTableLog = new HTable[tableN];
        try {
            for (int i = 0; i < tableN; i++) {
                wTableLog[i] = new HTable(HbaseUtil.getConf(), table_log_name);
                wTableLog[i].setWriteBufferSize(5 * 1024 * 1024); // 5MB
                wTableLog[i].setAutoFlush(false);
            }
            // putList = Collections.synchronizedList(new LinkedList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isWrite) {
            startWriteThread();
        }

    }

    static void startWriteThread() {
        for (int i = 0; i < threadN; i++) {
            final int a = i;
            Thread th = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            sleep(1000); // 1 second
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (wTableLog[a]) {
                            try {
                                wTableLog[a].flushCommits();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            th.setDaemon(true);
            th.start();
        }
    }

    // 鍒楃皣
    protected static String INFO_COLS = "I";
    protected static byte[] INFO_COLS_BYTES = Bytes.toBytes("I");
    // 鍒楅檺瀹氬悕

    public NewsDaoTest() {
        // TODO Auto-generated constructor stub

    }

    public NewsDaoTest(boolean isWrite) {
        NewsDaoTest.isWrite = isWrite;
    }

    public void addPut(Put put, String family, String quaifier, String value) {
        if (value != null) {
            put.add(Bytes.toBytes(family), Bytes.toBytes(quaifier), Bytes.toBytes(value));
        }
    }

    public void addPut(Put put, String family, String quaifier, int value) {
        put.add(Bytes.toBytes(family), Bytes.toBytes(quaifier), Bytes.toBytes(value));
    }

    public void addPut(Put put, String family, String quaifier, long value) {
        put.add(Bytes.toBytes(family), Bytes.toBytes(quaifier), Bytes.toBytes(value));
    }

    public void addPut(Put put, String family, String quaifier, boolean value) {
        put.add(Bytes.toBytes(family), Bytes.toBytes(quaifier), Bytes.toBytes(value));
    }

    public void Insert(News news) {
        try {
            byte[] rowkey;
            if (news.getId() == null) {
                String a = RowkeyUtil.rowkey(news.getTitleSrc());
                rowkey = Bytes.toBytes(a);
            } else {
                rowkey = Bytes.toBytes(news.getId());
            }

            // 澧炲姞涓�鏉¤褰�
            Put put = new Put(rowkey);

            addPut(put, INFO_COLS, NewsMap.MEDIA_TYPE, String.valueOf(news.getMediaType()));
            addPut(put, INFO_COLS, NewsMap.MEDIA_T_NAME, news.getMediaTname());

            addPut(put, INFO_COLS, NewsMap.TITLE_SRC, news.getTitleSrc());
            addPut(put, INFO_COLS, NewsMap.PUBDATE, String.valueOf(news.getPubdate()));
            addPut(put, INFO_COLS, NewsMap.TEXT_SRC, news.getTextSrc());

            addPut(put, INFO_COLS, NewsMap.WEBSITE_ID, news.getWebsiteId());
            addPut(put, INFO_COLS, NewsMap.MEDIA_NAME_SRC, news.getMediaNameSrc());
            addPut(put, INFO_COLS, NewsMap.MEDIA_NAME_ZH, news.getMediaNameZh());
            addPut(put, INFO_COLS, NewsMap.MEDIA_NAME_EN, news.getMediaNameEn());
            addPut(put, INFO_COLS, NewsMap.MEDIA_LEVEL, String.valueOf(news.getMediaLevel()));

            addPut(put, INFO_COLS, NewsMap.COUNTRY_NAME_ZH, news.getCountryNameZh());
            addPut(put, INFO_COLS, NewsMap.COUNTRY_NAME_EN, news.getCountryNameEn());
            addPut(put, INFO_COLS, NewsMap.PROVINCE_NAME_ZH, news.getProvinceNameZh());
            addPut(put, INFO_COLS, NewsMap.PROVINCE_NAME_EN, news.getProvinceNameEn());
            addPut(put, INFO_COLS, NewsMap.DISTRICT_NAME_ZH, news.getDistrictNameZh());
            addPut(put, INFO_COLS, NewsMap.DISTRICT_NAME_EN, news.getDistrictNameEn());

            addPut(put, INFO_COLS, NewsMap.LANGUAGECODE, news.getLanguageCode());
            addPut(put, INFO_COLS, NewsMap.LANGUAGE_T_NAME, news.getLanguageTname());

            addPut(put, INFO_COLS, NewsMap.AUTHOR, news.getAuthor());
            addPut(put, INFO_COLS, NewsMap.CREATED, String.valueOf(news.getCreated()));
            addPut(put, INFO_COLS, NewsMap.UPDATED, String.valueOf(news.getUpdated()));
            addPut(put, INFO_COLS, NewsMap.IS_ORIGINAL, String.valueOf(news.getIsOriginal()));
            addPut(put, INFO_COLS, NewsMap.VIEW, String.valueOf(news.getView()));
            addPut(put, INFO_COLS, NewsMap.URL, news.getUrl());
            addPut(put, INFO_COLS, NewsMap.DOC_LENGTH, String.valueOf(news.getDocLength()));

            addPut(put, INFO_COLS, NewsMap.TRANS_FROM_M, news.getTransFromM());
            addPut(put, INFO_COLS, NewsMap.PV, String.valueOf(news.getPv()));
            addPut(put, INFO_COLS, NewsMap.IS_HOME, String.valueOf(news.getIsHome()));
            addPut(put, INFO_COLS, NewsMap.IS_PICTURE, String.valueOf(news.getIsPicture()));

            addPut(put, INFO_COLS, NewsMap.COME_FROM, String.valueOf(news.getComeFrom()));

            addPut(put, INFO_COLS, NewsMap.COME_FROM_DB, String.valueOf(news.getComeFromDb()));
            addPut(put, INFO_COLS, NewsMap.USER_TAG, String.valueOf(news.getUserTag()));

            wTableLog[random.nextInt(tableN)].put(put);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public void Insert(News news ) {
    // try {
    // byte[] rowkey;
    // if(news.getId() == null){
    // String a = RowkeyUtil.rowkey(news.getTitleSrc());
    // rowkey = Bytes.toBytes(a);
    // }else{
    // rowkey = Bytes.toBytes( news.getId());
    // }
    //
    // //澧炲姞涓�鏉¤褰�
    // Put put = new Put(rowkey);
    //
    // addPut(put,INFO_COLS,NewsMap.MEDIA_TYPE,
    // String.valueOf(news.getMediaType() ));
    // addPut(put,INFO_COLS,NewsMap.MEDIA_T_NAME,
    // news.getMediaTname());
    //
    // addPut(put,INFO_COLS,NewsMap.TITLE_SRC,
    // news.getTitleSrc());
    // addPut(put,INFO_COLS,NewsMap.PUBDATE ,
    // String.valueOf(news.getPubdate() ) );
    // addPut(put,INFO_COLS,NewsMap.TEXT_SRC ,
    // news.getTextSrc( ));
    //
    // addPut(put,INFO_COLS,NewsMap.WEBSITE_ID ,
    // news.getWebsiteId());
    // addPut(put,INFO_COLS,NewsMap.MEDIA_NAME_SRC,
    // news.getMediaNameSrc());
    // addPut(put,INFO_COLS,NewsMap.MEDIA_NAME_ZH,
    // news.getMediaNameZh() );
    // addPut(put,INFO_COLS,NewsMap.MEDIA_NAME_EN,
    // news.getMediaNameEn() );
    // addPut(put,INFO_COLS,NewsMap.MEDIA_LEVEL,
    // String.valueOf(news.getMediaLevel() ));
    //
    // addPut(put,INFO_COLS,NewsMap.COUNTRY_NAME_ZH,
    // news.getCountryNameZh());
    // addPut(put,INFO_COLS,NewsMap.COUNTRY_NAME_EN,
    // news.getCountryNameEn());
    // addPut(put,INFO_COLS,NewsMap.PROVINCE_NAME_ZH,
    // news.getProvinceNameZh());
    // addPut(put,INFO_COLS,NewsMap.PROVINCE_NAME_EN,
    // news.getProvinceNameEn());
    // addPut(put,INFO_COLS,NewsMap.DISTRICT_NAME_ZH,
    // news.getDistrictNameZh());
    // addPut(put,INFO_COLS,NewsMap.DISTRICT_NAME_EN,
    // news.getDistrictNameEn());
    //
    // addPut(put,INFO_COLS,NewsMap.LANGUAGECODE,
    // news.getLanguageCode());
    // addPut(put,INFO_COLS,NewsMap.LANGUAGE_T_NAME,
    // news.getLanguageTname( ));
    //
    // addPut(put,INFO_COLS,NewsMap.AUTHOR,
    // news.getAuthor());
    // addPut(put,INFO_COLS,NewsMap.CREATED,
    // String.valueOf(news.getCreated() ));
    // addPut(put,INFO_COLS,NewsMap.UPDATED,
    // String.valueOf(news.getUpdated() ));
    // addPut(put,INFO_COLS,NewsMap.IS_ORIGINAL,
    // String.valueOf(news.isOriginal() ));
    // addPut(put,INFO_COLS,NewsMap.VIEW,
    // String.valueOf(news.getView()));
    // addPut(put,INFO_COLS,NewsMap.URL,
    // news.getUrl());
    // addPut(put,INFO_COLS,NewsMap.DOC_LENGTH,
    // String.valueOf(news.getDocLength()));
    //
    // addPut(put,INFO_COLS,NewsMap.TRANS_FROM_M,
    // news.getTransFromM());
    // addPut(put,INFO_COLS,NewsMap.PV,
    // String.valueOf(news.getPv() ));
    // addPut(put,INFO_COLS,NewsMap.IS_HOME,
    // String.valueOf(news.isHome() ));
    // addPut(put,INFO_COLS,NewsMap.IS_PICTURE,
    // String.valueOf(news.isPicture() ));
    //
    // addPut(put,INFO_COLS,NewsMap.COME_FROM,
    // String.valueOf(news.getComeFrom() ));
    //
    // addPut(put,INFO_COLS,NewsMap.COME_FROM_DB,
    // String.valueOf(news.getComeFromDb() ));
    // addPut(put,INFO_COLS,NewsMap.USER_TAG,
    // String.valueOf(news.getUserTag() ));
    //
    // putList.add(put);
    // if(putList.size()==100){
    // try {
    // wTableLog.put(putList);
    // putList.clear();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // public void InsertLeft() {
    // try {
    // wTableLog.put(putList);
    // putList.clear();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // 鏍规嵁rowkey寰楀埌涓�鏉℃柊闂�
    public News get(byte[] rowkey) {
        try {
            Get get = new Get(rowkey);
            Result r = wTableLog[random.nextInt(tableN)].get(get);

            News news = getNewsFromR(r);
            return news;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        }
        return null;
    }

    class AClass
            implements Runnable {

        List<String> rowkeyList;

        public AClass(List<String> rowkeyList) {
            this.rowkeyList = rowkeyList;
        }

        @Override
        public void run() {
            get(rowkeyList);
        }

    }

    public List<News> get(List<String> rowkeyList) {
        try {
            List<Get> getList = new ArrayList<>();
            for (int i = 0; i < rowkeyList.size(); i++) {
                Get get = new Get(Bytes.toBytes(rowkeyList.get(i)));
                getList.add(get);
            }
            long begin = System.currentTimeMillis();
            Result[] rs = wTableLog[random.nextInt(tableN)].get(getList);
            // List<News> resultList = new ArrayList<>();
            System.out
                    .println("该进程花费时间：" + (System.currentTimeMillis() - begin) + " 当前时间：" + System.currentTimeMillis());
            for (Result r : rs) {
                News news = getNewsFromR(r);
                // resultList.add(news);
            }
            // return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        }
        return null;
    }

    public News getByThreadN(List<String> rowkeyList, int threadN) {
        try {
            List<String>[] taskList = new ArrayList[threadN];
            for (int i = 0; i < taskList.length; i++) {
                taskList[i] = new ArrayList<>();
            }
            for (int i = 0; i < rowkeyList.size(); i++) {
                taskList[i % threadN].add(rowkeyList.get(i));
            }

            ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
            builder.setNameFormat("ParallelBatchQuery");
            ThreadFactory factory = builder.build();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadN, factory);
            long before = System.currentTimeMillis();
            System.out.println(before);
            for (int i = 0; i < threadN; i++)
                executor.execute(new AClass(taskList[i]));

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        }
        return null;
    }

    // 浠巖esult涓鍙栦竴鏉℃柊闂�
    public News getNewsFromR(Result r) {
        News news = new News(r.getRow(), r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.MEDIA_TYPE)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.MEDIA_T_NAME)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.TITLE_SRC)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.PUBDATE)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.TEXT_SRC)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.WEBSITE_ID)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.MEDIA_NAME_SRC)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.MEDIA_NAME_ZH)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.MEDIA_NAME_EN)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.MEDIA_LEVEL)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.COUNTRY_NAME_ZH)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.COUNTRY_NAME_EN)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.PROVINCE_NAME_ZH)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.PROVINCE_NAME_EN)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.DISTRICT_NAME_ZH)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.DISTRICT_NAME_EN)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.LANGUAGECODE)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.LANGUAGE_T_NAME)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.AUTHOR)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.CREATED)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.UPDATED)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.IS_ORIGINAL)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.VIEW)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.URL)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.DOC_LENGTH)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.TRANS_FROM_M)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.PV)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.IS_HOME)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.IS_PICTURE)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.COME_FROM)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.COME_FROM_DB)),
                r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.USER_TAG)));
        // System.out.println(news.getId() + " " + news.getMediaNameZh() + " " +
        // news.getTitleSrc() + " " + news.getCreated() + news.getTextSrc());

        return news;
    }

    public void countByDate(Date beginDate, Date endDate) {
        Map<String, Integer> sourceCount = new HashMap<>();

        try {
            long beginMills = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                String startF = String.format("%02d", i) + beginDate.getTime();
                String endF = String.format("%02d", i) + endDate.getTime();

                byte[] startRow = Bytes.padTail(Bytes.toBytes(startF), 20);
                byte[] endRow = Bytes.padTail(Bytes.toBytes(endF), 20);
                Scan scan = new Scan(startRow, endRow);
                // scan.setReversed(true);
                // Filter filter1 = new
                // SingleColumnValueFilter(Bytes.toBytes("I"),
                // Bytes.toBytes("comeFromDb"),
                // CompareOp.EQUAL, Bytes.toBytes("CisionHttp"));
                Filter filter1 = new SingleColumnValueFilter(Bytes.toBytes("I"), Bytes.toBytes("pubdate"),
                        CompareOp.GREATER_OR_EQUAL, Bytes.toBytes("2016-05-01 00:00:00"));
                // Filter filter2 = new SingleColumnValueFilter(
                // Bytes.toBytes("I"),
                // Bytes.toBytes("languageTName"), CompareOp.EQUAL,
                // Bytes.toBytes(""));
                FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
                filterList.addFilter(filter1);
                // filterList.addFilter(filter2);
                scan.setFilter(filterList);
                ResultScanner rs = wTableLog[random.nextInt(tableN)].getScanner(scan);
                for (Result r : rs) {
                    News news = getNewsFromR(r);
                    if (news.getComeFromDb().equals("CisionHttp"))
                        System.out.println(news.getPubdate());
                }
            }
            System.out.println("time spend : " + (System.currentTimeMillis() - beginMills) / 1000);
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
    }

    public void handleAll(FilterList filterList) {
        try {
            long beginMills = System.currentTimeMillis();
            Scan scan = new Scan();
            if (filterList != null) {
                scan.setFilter(filterList);
            }
            // scan.setStartRow(Bytes.toBytes("90"));
            // scan.setReversed(true);
            // scan.setCaching(400000);
            ResultScanner rs = wTableLog[random.nextInt(tableN)].getScanner(scan);
            for (Result r : rs) {
                News news = getNewsFromR(r);
                handleNews(news);
            }
            System.out.println("time spend : " + (System.currentTimeMillis() - beginMills) / 1000);

        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
    }

    public void handleNews(News news) {
    }

    public void countAll() {
        Map<String, Integer> sourceCount = new HashMap<>();
        Map<String, Integer> sourceCountTemp = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File file = new File("count" + sdf.format(new Date(System.currentTimeMillis())) + ".txt");

        try {
            long beginMills = System.currentTimeMillis();
            Scan scan = new Scan();
            Filter filter1 = new SingleColumnValueFilter(Bytes.toBytes("I"), Bytes.toBytes("comeFrom"), CompareOp.EQUAL,
                    Bytes.toBytes("Cision"));
            // Filter filter2 = new SingleColumnValueFilter(
            // Bytes.toBytes("I"),
            // Bytes.toBytes("languageTName"), CompareOp.EQUAL,
            // Bytes.toBytes(""));
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            filterList.addFilter(filter1);
            // filterList.addFilter(filter2);
            scan.setFilter(filterList);
            ResultScanner rs = wTableLog[random.nextInt(tableN)].getScanner(scan);
            int loopToSave = 0;
            for (Result r : rs) {
                String comeFrom = Bytes.toString(r.getValue(Bytes.toBytes("I"), Bytes.toBytes("comeFrom")));
                String comeFromDb = Bytes.toString(r.getValue(Bytes.toBytes("I"), Bytes.toBytes("comeFromDb")));
                String mediaTname = Bytes.toString(r.getValue(Bytes.toBytes("I"), Bytes.toBytes("mediaTname")));
                String languageTname = Bytes
                        .toString(r.getValue(Bytes.toBytes(INFO_COLS), Bytes.toBytes(NewsMap.LANGUAGE_T_NAME)));
                StringBuffer sb = new StringBuffer();

                String count_1st = null;
                String count_2nd = null;
                String count_3rd = null;

                loopToSave++;
                if (comeFrom.equals("Cision")) {
                    sb.append(comeFromDb);
                } else
                    sb.append(comeFrom);

                count_1st = sb.toString();

                sb.append("_").append(mediaTname);
                count_2nd = sb.toString();
                addCount(sourceCountTemp, count_2nd, 1);

                sb.append("_").append(languageTname);
                count_3rd = sb.toString();
                addCount(sourceCountTemp, count_3rd, 1);

                addCount(sourceCountTemp, count_1st, 1);
                if (loopToSave == 100000) {
                    addCount(sourceCount, sourceCountTemp);
                    sourceCountTemp.clear();
                    saveFile(sourceCount, file);
                }

            }
            addCount(sourceCount, sourceCountTemp);
            saveFile(sourceCount, file);
            System.out.println("time spend : " + (System.currentTimeMillis() - beginMills) / 1000);

        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
    }

    public void countBetween(Date beginDate, Date endDate, Map<String, Integer> sourceCount) {

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // File file = new File("/home/wwwroot/default/" + "count" +
        // sdf.format(endDate) + ".txt");
        // Map<String, Integer> sourceCountTemp = new HashMap<>();
        // try {
        // long beginMills = System.currentTimeMillis();
        // for (int i = 0; i < 100; i++) {
        // String startF = null;
        // if (beginDate == null) {
        // startF = String.format("%02d", i) + (endDate.getTime() - 24 * 60 * 60
        // * 1000);
        // } else {
        // startF = String.format("%02d", i) + beginDate.getTime();
        // }
        // String endF = String.format("%02d", i) + endDate.getTime();
        //
        // KeyOnlyFilter filter = new KeyOnlyFilter();
        //
        // byte[] startRow = Bytes.padTail(Bytes.toBytes(startF), 20);
        // byte[] endRow = Bytes.padTail(Bytes.toBytes(endF), 20);
        // Scan scan = new Scan(startRow, endRow);
        // // Filter filter1 = new SingleColumnValueFilter(
        // // Bytes.toBytes("I"),
        // // Bytes.toBytes("comeFrom"), CompareOp.EQUAL,
        // // Bytes.toBytes("Cision"));
        // // Filter filter2 = new SingleColumnValueFilter(
        // // Bytes.toBytes("I"),
        // // Bytes.toBytes("languageTName"), CompareOp.EQUAL,
        // // Bytes.toBytes(""));
        // // FilterList filterList = new FilterList(
        // // FilterList.Operator.MUST_PASS_ALL);
        // // filterList.addFilter(filter1);
        // // filterList.addFilter(filter2);
        // // scan.setFilter(filterList);
        // // scan.setFilter(filter);
        // ResultScanner rs =
        // wTableLog[random.nextInt(tableN)].getScanner(scan);
        // for (Result r : rs) {
        // String comeFrom = Bytes.toString(r.getValue(Bytes.toBytes("I"),
        // Bytes.toBytes("comeFrom")));
        // String comeFromDb = Bytes.toString(r.getValue(Bytes.toBytes("I"),
        // Bytes.toBytes("comeFromDb")));
        // String mediaTname = Bytes.toString(r.getValue(Bytes.toBytes("I"),
        // Bytes.toBytes("mediaTname")));
        // String languageTname = Bytes
        // .toString(r.getValue(Bytes.toBytes(INFO_COLS),
        // Bytes.toBytes(NewsMap.LANGUAGE_T_NAME)));
        // StringBuffer sb = new StringBuffer();
        //
        // String count_1st = null;
        // String count_2nd = null;
        // String count_3rd = null;
        //
        // if (comeFrom.equals("Cision")) {
        // sb.append(comeFromDb);
        // } else
        // sb.append(comeFrom);
        //
        // count_1st = sb.toString();
        //
        // sb.append("_").append(mediaTname);
        // count_2nd = sb.toString();
        // addCount(sourceCountTemp, count_2nd, 1);
        //
        // sb.append("_").append(languageTname);
        // count_3rd = sb.toString();
        // addCount(sourceCountTemp, count_3rd, 1);
        //
        // addCount(sourceCountTemp, count_1st, 1);
        //
        // }
        // rs.close();
        // }
        // System.out.println("Hbase time spend : " +
        // (System.currentTimeMillis() - beginMills) / 1000);
        // addCount(sourceCount, sourceCountTemp);
        // System.out.println("Count time spend : " +
        // (System.currentTimeMillis() - beginMills) / 1000);
        // saveFile(sourceCount, file);
        // System.out.println("Save time spend : " + (System.currentTimeMillis()
        // - beginMills) / 1000);
        //
        // } catch (Exception e) {
        // LOG.error(e);
        // e.printStackTrace();
        // }
    }

    public void saveFile(Map<String, Integer> sourceCount, File file)
            throws IOException {
        BufferedWriter bf = new BufferedWriter(new FileWriter(file));
        StringBuffer sb2 = new StringBuffer();
        for (Map.Entry<String, Integer> entry : sourceCount.entrySet()) {
            sb2.delete(0, sb2.length());
            sb2.append(entry.getKey()).append(":").append(entry.getValue());
            bf.write(sb2.toString());
            bf.newLine();
        }
        bf.flush();
        bf.close();
    }

    protected synchronized void addCount(Map<String, Integer> sourceCount, Map<String, Integer> sourceCountTemp) {
        // TODO Auto-generated method stub
        for (Map.Entry<String, Integer> entry : sourceCountTemp.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            System.out.println(key + " : " + value);
            int i;
            try {

                i = sourceCount.get(key);
                i = i + value;
                sourceCount.put(key, i);
            } catch (Exception e) {
                i = value;
                sourceCount.put(key, i);
            }
        }
    }

    public synchronized void addCount(Map<String, Integer> map, String key, int count) {
        String key2 = key.substring(0, key.lastIndexOf('_')) + "_已导入";
        int i;
        try {
            i = map.get(key);
            i = i + count;
            map.put(key, i);
        } catch (Exception e) {
            i = count;
            map.put(key, i);
        }
        try {
            i = map.get(key2);
            i = i + count;
            map.put(key2, i);
        } catch (Exception e) {
            i = count;
            map.put(key2, i);
        }
    }

    // 鏍规嵁鏌愭鏃堕棿鑾峰緱涓�娈垫椂闂寸殑鏂伴椈
    public List<News> getByDate(Date beginDate, Date endDate) {
        List<News> result = new ArrayList<News>();
        try {
            long beginMills = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                String startF = String.format("%02d", i) + beginDate.getTime();
                String endF = String.format("%02d", i) + endDate.getTime();

                byte[] startRow = Bytes.padTail(Bytes.toBytes(startF), 20);
                byte[] endRow = Bytes.padTail(Bytes.toBytes(endF), 20);
                Scan scan = new Scan(startRow, endRow);
                Filter filter1 = new SingleColumnValueFilter(Bytes.toBytes("I"), Bytes.toBytes("comeFrom"),
                        CompareOp.EQUAL, Bytes.toBytes("Cision"));
                // Filter filter2 = new SingleColumnValueFilter(
                // Bytes.toBytes("I"),
                // Bytes.toBytes("languageTName"), CompareOp.EQUAL,
                // Bytes.toBytes(""));
                FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
                filterList.addFilter(filter1);
                // filterList.addFilter(filter2);
                scan.setFilter(filterList);
                ResultScanner rs = wTableLog[random.nextInt(tableN)].getScanner(scan);
                for (Result r : rs) {
                    News news = getNewsFromR(r);
                    result.add(news);
                }
            }
            System.out.println("time spend : " + (System.currentTimeMillis() - beginMills) / 1000);
            System.err.println(result.size());
            return result;
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return result;
    }

    public int getNumByDate(Date beginDate, Date endDate) {
        int count = 0;
        try {
            long beginMills = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                String startF = String.format("%02d", i) + beginDate.getTime();
                String endF = String.format("%02d", i) + endDate.getTime();

                byte[] startRow = Bytes.padTail(Bytes.toBytes(startF), 20);
                byte[] endRow = Bytes.padTail(Bytes.toBytes(endF), 20);
                Scan scan = new Scan(startRow, endRow);
                // Filter filter1 = new SingleColumnValueFilter(
                // Bytes.toBytes("I"),
                // Bytes.toBytes("comeFrom"), CompareOp.EQUAL,
                // Bytes.toBytes("Goonie"));
                // scan.setFilter(filter1);
                ResultScanner rs = wTableLog[random.nextInt(tableN)].getScanner(scan);
                for (Result r : rs) {
                    count++;
                }
            }
            System.out.println("time spend : " + (System.currentTimeMillis() - beginMills) / 1000);
            System.err.println(count);
            return count;
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return count;
    }

    public static void create() {
        try {
            String tablename = "NewsArticleBE2";
            String[] familys = {"I"};
            HbaseUtil.createTable(tablename, familys);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delRows(Date beginDate, Date endDate)
            throws Exception {
        List<String> rowkeyList = new LinkedList<>();
        try {
            long beginMills = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                String startF = String.format("%02d", i) + beginDate.getTime();
                String endF = String.format("%02d", i) + endDate.getTime();

                byte[] startRow = Bytes.padTail(Bytes.toBytes(startF), 20);
                byte[] endRow = Bytes.padTail(Bytes.toBytes(endF), 20);
                Scan scan = new Scan(startRow, endRow);
                Filter filter1 = new SingleColumnValueFilter(Bytes.toBytes("I"), Bytes.toBytes("comeFromDb"),
                        CompareOp.EQUAL, Bytes.toBytes("CisionHttp"));
                // Filter filter2 = new SingleColumnValueFilter(
                // Bytes.toBytes("I"),
                // Bytes.toBytes("languageTName"), CompareOp.EQUAL,
                // Bytes.toBytes(""));
                FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
                filterList.addFilter(filter1);
                // filterList.addFilter(filter2);
                scan.setFilter(filterList);
                ResultScanner rs = wTableLog[random.nextInt(tableN)].getScanner(scan);
                for (Result r : rs) {
                    News news = getNewsFromR(r);

                    rowkeyList.add(Bytes.toString(r.getRow()));
                }
            }
            System.out.println("time spend : " + (System.currentTimeMillis() - beginMills) / 1000);
            System.err.println(rowkeyList.size());
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
        HbaseUtil.delRows("NewsArticleBE2", rowkeyList);
    }

    public static Map<String, String> getDicMap(String filePath)
            throws IOException {

        Map<String, String> map = new HashMap<String, String>();
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;

        while ((line = br.readLine()) != null) {
            try {
                String[] str = line.split("\\|");
                String url = str[9].substring(str[9].lastIndexOf('(') + 1, str[9].lastIndexOf(')'))
                        .replaceAll("\\u00A0*$", "").replaceAll("^\\u00A0*", "").trim()
                        .replaceAll("^(http(s)?:(//|\\\\\\\\))?www.*?\\.", "");
                map.put(str[0], url);
            } catch (Exception e) {
                System.err.println("导入 ————出错了");
            }
        }
        br.close();
        return map;
    }

    public static void main(String[] agrs)
            throws Exception {

        // 输入日期的方式：
        // NewsDao newsDao= new NewsDao();
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //
        // System.out.println("输入日期：");
        // Scanner s = new Scanner(System.in);
        // Date endDate = null;
        // while(true){
        // String line = s.nextLine();
        // System.out.println(line.length());
        // try {
        // if(line.length()==4){
        // endDate = sdf.parse("2016"+line+ "180000");
        // break;
        // }
        // if(line.length()==8){
        // endDate = sdf.parse(line+ "180000");
        // break;
        // }
        // if(line.length()==14){
        // endDate = sdf.parse(line);
        // break;
        // }
        // } catch (Exception e) {
        // // TODO: handle exception
        // e.printStackTrace();
        // }
        // }
        // newsDao.countBetween(null, endDate);

        // newsDao.countAll();
        // HbaseUtil.getAllRecord("NewsArticleBE");
        // HbaseUtil.getAllRecordNum("NewsArticleBE");

        // HbaseUtil.deleteTable("NewsArticleBE");
        // newsDao.create();
        //
        // **鎸夋椂闂存煡鏁版嵁**//*
        // NewsDao newsDao= new NewsDao();
        // Date beginDate = new Date();
        // Date endDate = new Date(beginDate.getTime()-100*60*1000);
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        // beginDate = dateFormat.parse("2016-06-16 12:00:00");
        // endDate = dateFormat.parse("2016-06-20 12:00:00");
        //
        // System.out.println( dateFormat.format(beginDate)+
        // dateFormat.format(endDate));
        // newsDao.countByDate(beginDate, endDate);
        // newsDao.delRows( beginDate, endDate );

        // newsDao.getNumByDate(beginDate, endDate);

        // insertBeihang();

        // System.out.println("max : \t"+ Runtime.getRuntime().maxMemory() );
        // System.out.println("free : \t"+ Runtime.getRuntime().freeMemory() );
        // System.out.println("total : \t"+ Runtime.getRuntime().totalMemory()
        // );

    }

}
