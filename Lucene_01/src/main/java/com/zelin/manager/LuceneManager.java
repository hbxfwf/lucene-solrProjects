package com.zelin.manager;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 主要是对索引库进行操作的类
 * @Date: Create in 2019/4/18 10:13
 */
public class LuceneManager {
    /*-----------------------------第一部分：对索引库的基本操作------------------------------*/
    //0、添加一个文档到索引库
    public void addIndexOne() throws Exception{
        //1.得到索引库的输出流对象
        IndexWriter writer = getIndexWriter();
        //2.构造一个文档对象
        Document document = new Document();
        //3.构造一组域对象，用于添加到此文档对象中
        Field fileNameField = new TextField("fileNameField","新添加的文件名域", Field.Store.YES);
        Field fileSizeField = new LongField("fileSizeField",100, Field.Store.YES);
        //4.将上面的文档域与文档绑定
        document.add(fileNameField);
        document.add(fileSizeField);
        //5.将文档添加到索引库
        writer.addDocument(document);
        //6.关闭流
        writer.close();
    }
    //1、添加一组文档到索引库
    public void addIndex() throws Exception {
        //得到索引库的输出流对象
        IndexWriter writer = getIndexWriter();
        //4.构造document对象
        //4.1)遍历searchdocument存放原始文档的目录
        File dirs = new File("e:/searchdocument");
        for (File file : dirs.listFiles()) {
            //4.2)构造一个文档对象
            Document document = new Document();
            //4.3)得到文件的各个相关属性（只关注域中用到的）
            String fileName = file.getName();
            String filePath = file.getAbsolutePath();
            String fileContent = FileUtils.readFileToString(file,"utf-8");
            long fileSize = FileUtils.sizeOf(file);
            //4.4)利用上面的内容构造各个域
            //① 定义文件名域
            Field fileNameField = new TextField("fileNameField",fileName, Field.Store.YES);
            //② 定义文件路径域
            Field filePathField = new StoredField("filePathField",filePath);
            //③ 定义文件内容域
            Field fileContentField = new TextField("fileContentField",fileContent, Field.Store.YES);
            //④ 定义文件大小域
            Field fileSizeField = new LongField("fileSizeField",fileSize, Field.Store.YES);
            //4.5)将上面的各个域添加到document对象
            document.add(fileNameField);
            document.add(filePathField);
            document.add(fileContentField);
            document.add(fileSizeField);
            //4.6)将此文件写入到索引库中
            writer.addDocument(document);
        }
        //5.关闭流
        writer.close();
    }
    //2、进行索引库的查询
    public void queryIndex(String keywords) throws IOException {
        //得到索引查询对象
        IndexSearcher searcher = getIndexSearcher();
        //4.定义要查询的对象
        Query query = new TermQuery(new Term("fileContentField",keywords));
        //5.返回查询到的结果对象（此结果对象中包含查询到的文档id），参数2：代表查询的记录数
        TopDocs topDocs = searcher.search(query, 10);
        //6.得到查询出的实际记录数
        int totalHits = topDocs.totalHits;
        System.out.println("实际查询的记录为：" + totalHits);
        //7.遍历topDocs，得到文档对象
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //7.1)得到文档的id值
            int id = scoreDoc.doc;
            //7.2)使用search对象得到文档
            Document doc = searcher.doc(id);
            //7.3)调用打印对象的方法进行打印
            printDoc(doc);
        }
        //8.关闭流
        searcher.getIndexReader().close();
    }
    //3.修改索引库（原理：先删除指定term，再添加指定的document）
    public void updateIndex() throws IOException {
        //1.得到IndexWriter对象
        IndexWriter indexWriter = getIndexWriter();
        //2.指定要修改的term（按条件查询出来，再删除）
        Term term = new Term("fileNameField","spring");
        //3.定义要添加的文档
        Document document = new Document();
        //4.定义要添加到文档的域(如果以前没有AA和BB这两个域，则会在每个文档中增加这两个域)
        Field fileNameField = new TextField("AA","修改的fileNameField", Field.Store.YES);
        Field fileContentField = new TextField("BB","修改的fileContentField", Field.Store.YES);
        //5.将文档对象与文件域进行绑定
        document.add(fileNameField);
        document.add(fileContentField);
        //6.修改索引库
        indexWriter.updateDocument(term,document);
        //7.关闭流
        indexWriter.close();
    }
    //4.删除索引库指定文档
    public void deleteIndex() throws IOException {
        //1.得到输出流
        IndexWriter writer = getIndexWriter();
        //2.制造查询条件（根据此条件查询得到的结果要被删除）
        Term term = new Term("fileNameField","web");
        //3.删除索引库
        writer.deleteDocuments(term);
        //4.关闭流
        writer.close();
    }
    //5.删除索引库所有文档
    public void deleteAll() throws Exception{
        //1.得到输出流
        IndexWriter writer = getIndexWriter();
        //2.删除索引库
        writer.deleteAll();
        //3.关闭流
        writer.close();
    }
    //打印文档对象
    private void printDoc(Document doc) {
        System.out.println("--------------------------------------------------------------");
        //1.得到文档的各个域
        IndexableField fileNameField = doc.getField("fileNameField");
        IndexableField fileContentField = doc.getField("fileContentField");
        IndexableField filePathField = doc.getField("filePathField");
        IndexableField fileSizeField = doc.getField("fileSizeField");
        //2.得到各个域的值
        String fileName = fileNameField.stringValue();
        String fileContent = fileContentField.stringValue();
        String filePath = filePathField.stringValue();
        String fileSize = fileSizeField.stringValue();
        //3.打印各个值
        System.out.println("fileName:" + fileName);
        //System.out.println("fileContent:" + fileContent);
        System.out.println("filePath:" + filePath);
        System.out.println("fileSize:" + fileSize);
        System.out.println("--------------------------------------------------------------");
    }
    //得到索引查询对象
    private IndexSearcher getIndexSearcher() throws IOException {
        //1.定义要查询的索引库目录对象
        Directory directory = FSDirectory.open(new File("e:/myindex"));
        //2.定义要查询的输入流对象
        IndexReader reader = DirectoryReader.open(directory);
        //3.通过上面的输入流得到索引查询器对象
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
    //得到索引库的输出流对象
    private IndexWriter getIndexWriter() throws IOException {
        //1.定义索引库的目录位置
        Directory directory = FSDirectory.open(new File("e:/myindex"));
        //2.定义输出流的配置对象
        //2.1)定义分词器对象
        Analyzer analyzer = new StandardAnalyzer();
        //2.2)定义输出流的配置对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,analyzer) ;
        //3.构造输出流对象
        return new IndexWriter(directory,config);
    }
    /*-----------------------------第二部分：按照Query的子类进行查询------------------------------*/
    //1、使用Query的子类MatchAllDocsQuery进行查询：
    public void queryByMatchAllDocs() throws IOException {
        //1.得到searcher对象
        IndexSearcher indexSearcher = getIndexSearcher();
        //2.得到Query的子类对象
        Query query = new MatchAllDocsQuery();
        //3.根据indexSeacher与query进行查询并打印
        printByQuery(indexSearcher, query);
        //4.关闭流
        indexSearcher.getIndexReader().close();
    }
    //2.通过Query的子类TermQuery进行查询
    public void queryByTermQuery() throws Exception{
        //1.得到searcher对象
        IndexSearcher indexSearcher = getIndexSearcher();
        //2.得到TermQuery对象
        Query termQuery = new TermQuery(new Term("fileContentField","lucene"));
        //3.查询并打印
        printByQuery(indexSearcher,termQuery);
        //4.关闭流
        indexSearcher.getIndexReader().close();
    }
    //3.通过Query的子类NumericRangeQuery完成指定数值范围的查询
    public void queryByNumericRangeQuery () throws Exception{
        //1.得到searcher对象
        IndexSearcher indexSearcher = getIndexSearcher();
        //2.得到TermQuery对象
        //参数说明：
        //① 代表要查询的字段（域名）
        //② 代表查询的开始值（最小值）
        //③ 代表查询的结束值（最大值）
        //④ 代表查询是否包含开始值
        //⑤ 代表查询是否包含结束值
        Query numericRangeQuery = NumericRangeQuery.newLongRange("fileSizeField",0L,100L,true,true);
        //3.查询并打印
        printByQuery(indexSearcher,numericRangeQuery);
        //4.关闭流
        indexSearcher.getIndexReader().close();
    }
    //4.使用BooleanQuery进行组合条件查询
    //需求：查询0-100字节并且内容之中包含有spring的的文档信息
    public void queryByBooleanQuery() throws Exception{
        //1.得到searcher对象
        IndexSearcher indexSearcher = getIndexSearcher();
        //2.定义查询条件
        Query termQuery = new TermQuery(new Term("fileContentField","web"));
        Query numericRangeQuery = NumericRangeQuery.newLongRange("fileSizeField",0L,100L,true,true);
        //3.定义BooleanQuery用于组合上面两个条件
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(termQuery, BooleanClause.Occur.SHOULD);        //BooleanClause.Occur.SHOULD：相当于or
        booleanQuery.add(numericRangeQuery, BooleanClause.Occur.MUST); //BooleanClause.Occur.MUST: 相当于and
        //4.开始查询并打印
        printByQuery(indexSearcher,booleanQuery);
        //5.关闭流
        indexSearcher.getIndexReader().close();
    }
    //通过传入indexSearcher与query子类的对象进行打印输出查询到的结果
    private void printByQuery(IndexSearcher indexSearcher, Query query) throws IOException {
        //1.使用searcher进行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        //2.遍历上面的查询结果
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //2.1)得到文档的id
            int id = scoreDoc.doc;
            //2.2)根据文档id得到文档对象
            Document document = indexSearcher.doc(id);
            //2.3)打印文档对象
            printDoc(document);
        }
    }

    /*-----------------------------第三部分：使用queryParser进行查询------------------------------*/
    //1.通过queryParser进行查询
    public void findDocsByQueryParser() throws Exception{
        //1.得到searcher对象
        IndexSearcher indexSearcher = getIndexSearcher();
        //2.构造查询分析器对象queryParser
        //参数说明：
        //① 代表要查询的字段名（域名）
        //② 代表分词器
        QueryParser queryParser = new QueryParser("fileContentField",new IKAnalyzer());
        //3.根据上面的查询分析器对象分析得到一个Query对象
        //说明：分词结果为：java lucene,即将来查询fileContentField这个域中含有java与lucene的文档
        Query query = queryParser.parse("java is lucene");
        //4.打印并输出
        printByQuery(indexSearcher,query);
        //5.关闭流
        indexSearcher.getIndexReader().close();
    }
    //2.通过queryParser进行查询(使用简易语法)
    public void findDocsByQueryParser2() throws Exception{
        //1.得到searcher对象
        IndexSearcher indexSearcher = getIndexSearcher();
        //2.构造查询分析器对象queryParser
        //参数说明：
        //① 代表要查询的字段名（域名）
        //② 代表分词器
        QueryParser queryParser = new QueryParser("fileContentField",new IKAnalyzer());
        //3.通过简易语法在查询分析器中指定查询条件
        Query query = queryParser.parse("fileNameField:java +fileContentField:lucene");
        //4.打印并输出
        printByQuery(indexSearcher,query);
        //5.关闭流
        indexSearcher.getIndexReader().close();
    }

    public void findDocsByMultiFieldQueryParser() throws Exception{
        //1.得到searcher对象
        IndexSearcher indexSearcher = getIndexSearcher();
        //2.定义要分析的多域字段
        String[] fields = {"fileNameField","fileContentField"};
        //3.创建查询分析器
        QueryParser queryParser = new MultiFieldQueryParser(fields,new IKAnalyzer());
        //4.通过查询分析器得到查询对象
        Query query = queryParser.parse("java is lucene");
        //5.进行多域查询
        printByQuery(indexSearcher,query);
        //6.关闭流
        indexSearcher.getIndexReader().close();
    }
}
