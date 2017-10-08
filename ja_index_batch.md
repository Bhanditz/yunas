1.[Yunas Tutorial(基本設定)](/ja_index.md)   
2.[Yunas Tutorial(DB)](/ja_index_db.md)  
3.[Yunas Tutorial(バッチ)](/ja_index_batch.md)  

[English](/)

# Tutorial(バッチ)

以下のようにバッチを登録することが可能です。

```
fun main(args: Array<String>) {

   
    YunasBatch.add("test",{(args) ->
        println("HelloWorld")
    } )

    YunasBatch.run(args)
}
```

実行可能Jarにした後、yunas.batchオプションに上記で登録したバッチ名を
渡すと実行されます。　　
```
java -Dyunas.batch=test -jar xxxxx.jar
```

**Yunas WebとYunasBatchを同一Jarで併用することは推奨していません**