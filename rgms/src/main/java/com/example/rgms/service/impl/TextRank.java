package com.example.rgms.service.impl;

import lombok.Data;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;


@Component
public class TextRank {
    public  String  textRank(String content){
        if (content==null){
            return  null;
        }

        List<String> keyWords = new ArrayList<>();
        String result="";
        int k = 2;  //窗口大小/2
        float d = 0.85f;
        /**
         * 标点符号、常用词、以及“名词、动词、形容词、副词之外的词”
         */
        Set<String> stopWordSet = new HashSet<String>();

        stopWordSet.add("都");
        stopWordSet.add("和");
        stopWordSet.add("为");
        stopWordSet.add("让");
        stopWordSet.add("在");
        stopWordSet.add("由");
        stopWordSet.add("上");
        stopWordSet.add("是");
        stopWordSet.add("的");
        stopWordSet.add("地");
        stopWordSet.add("从");
        stopWordSet.add("将");
        stopWordSet.add("但");



        String field=content;

        Analyzer analyzer = new IKAnalyzer(true);
        TokenStream ts = null;
        //分词
        try {
            ts = analyzer.tokenStream("myfield", new StringReader(field));
            OffsetAttribute offset = (OffsetAttribute) ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute term = (CharTermAttribute) ts.addAttribute(CharTermAttribute.class);
            TypeAttribute type = (TypeAttribute) ts.addAttribute(TypeAttribute.class);
            ts.reset();

            while (ts.incrementToken()) {
                if (!stopWordSet.contains(term.toString())) {
                    keyWords.add(term.toString());
                }
            }
            ts.end();
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if (ts != null) {
                try {
                    ts.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

        Map<String, Set<String>> relationWords = new HashMap<>();


        //获取每个关键词 前后k个的组合
        for (int i = 0; i < keyWords.size(); i++) {
            String keyword = keyWords.get(i);
            Set<String> keySets = relationWords.get(keyword);
            if (keySets == null) {
                keySets = new HashSet<>();
                relationWords.put(keyword, keySets);
            }

            for (int j = i - k; j <= i + k; j++) {
                if (j < 0 || j >= keyWords.size() || j == i) {
                    continue;
                } else {
                    keySets.add(keyWords.get(j));
                }
            }
        }

       /* for (String s : relationWords.keySet()) {
            System.out.print(s+" ");
            for (String s1 : relationWords.get(s)) {
                System.out.print(s1+" ");
            }
            System.out.println();
        }*/


        Map<String, Float> score = new HashMap<>();
        float min_diff = 0.1f; //差值最小
        int max_iter = 100;//最大迭代次数

        //迭代
        for (int i = 0; i < max_iter; i++) {
            Map<String, Float> m = new HashMap<>();
            float max_diff = 0;
            for (String key : relationWords.keySet()) {
                Set<String> value = relationWords.get(key);
                //先给每个关键词一个默认rank值
                m.put(key, 1 - d);
                //一个关键词的TextRank由其它成员投票出来
                for (String other : value) {
                    int size = relationWords.get(other).size();
                    if (key.equals(other) || size == 0) {
                        continue;
                    } else {
                        m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                    }
                }
//                System.out.println("m.get(key):"+m.get(key)+" score:"+(score.get(key) == null ? 0 : score.get(key)));
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }

            score = m;
            if (max_diff <= min_diff) {
                System.out.println("迭代次数：" + i);
                break;
            }
        }

        List<Score> scores = new ArrayList<>();
        for (String s : score.keySet()) {
            Score score1 = new Score();
            score1.key = s;
            score1.significance = score.get(s);
            scores.add(score1);
        }

        scores.sort(new Comparator<Score>() {
            @Override
            public int compare(Score o1, Score o2) {
                if (o2.significance - o1.significance > 0) {
                    return 1;
                } else {
                    return -1;
                }

            }
        });

        for (Score score1 : scores) {
            System.out.println(score1);
        }
        for(int i=0;i<5;i++){
            if(scores.size()>i){
                result=result+scores.get(i).key+" ";
            }
        }

        return result;

    }
//    public static void main(String[] args) {
//        String field="计算机技术以其强大的实用性得到了快速发展，目前计算机技术在各 " +
//                " 领域得到了普及，在各种生活场景中发挥着极其重要的作用，为人们生产生活提供了重要的技术保 " +
//                " 障，改变了人们的生活方式，提高了人们的生活质量[1] 。计算机软件作为计算机重要的部分，发挥着计算机的核心 " +
//                " 作用，而操作系统与应用软件作为计算机能力发挥的重要保障，通过计算机实现各种所需要的功能。2??计算机软件开发技术发展现状 " +
//                " 随着计算机的快速稳定发展，为了适应计算机技术的发展需要，强化对计算机软件开发顺应了技术发展趋势，同时也得到了各领域的关注和重视[2] 。 " +
//                " 在信息全球化的冲击和影响下，计算机软件开发技术遇到了难题，对于其技" +
//                " 术的发展带来较大的困扰，具体主要体现在：（1）缺乏" +
//                " 核心技术：与西方发达国家相比较而言，国内计算机软件" +
//                " 开发技术起步晚，核心技术方面还有待研发，导致计算机" +
//                " 软件技术整体水平不高，仍然处于相对较低的阶段，导致" +
//                " 计算机软件开发质量难以得到保障；（2）人才结构不合" +
//                " 理：在计算机软件开发过程中，需要技术人员提供技术方" +
//                " 面的支持。从技术人员引进的角度来讲，往往需要大量的" +
//                " 资金，并且需要较长的技术开发周期，鉴于当前这些问题" +
//                " 的'现，导致科研人员结构调整难度增加；（3）缺乏完" +
//                " 善的产品体系：从目前国内计算机软件开发的角度来讲，" +
//                " 由于缺乏关键的核心技术支持，无法实现技术方面的保障，" +
//                " 难以建立完善的软件产品体系" +
//                " [3] ；（4）计算机软件开发" +
//                " 环境需完善：随着计算机软件开发不断发展，计算机硬件" +
//                " 投入比例远远大于软件投入，但软件开发往往受到诸'多方" +
//                " 面因素的限制和影响，其中较为常见的有知识产权保护、" +
//                " 价格等，在当前这些问题的影响下，软件开发环境出现恶" +
//                " 化；（5）软件成本较高：随着计算机软件开发不断开展，" +
//                " 软件成本处于居高不下的状态，这也是当前计算机软件开" +
//                " 发面临的主要问题，对于行业的发展有着较大的影响" +
//                " [4] 。";
//        String res=textRank(field);
//        System.out.println("前5个关键词: "+res);
//    }
    
}

class Score {
    String key;
    float significance;

    @Override
    public String toString() {
        return "关键词=" + key +
                ", 重要程度=" + significance;
    }
}
