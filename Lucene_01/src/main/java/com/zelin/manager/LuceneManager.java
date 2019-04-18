package com.zelin.manager;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 主要是对索引库进行操作的类
 * @Date: Create in 2019/4/18 10:13
 */
public class LuceneManager {
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
            String fileContent = FileUtils.readFileToString(file);
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
}
