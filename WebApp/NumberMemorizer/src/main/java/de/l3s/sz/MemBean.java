package de.l3s.sz;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.l3s.sz.memorizer.Indexer;
import de.l3s.sz.memorizer.MEMOMAPPING;
import de.l3s.sz.memorizer.Result;

@ManagedBean(name = "mem", eager = true)
@ViewScoped
public class MemBean implements Serializable {

	static private HashMap<String, Indexer> indexers = null;
	
	public  Set<String> getIndexers() {
		return indexers.keySet();
	}
	public  void setIndexers(HashMap<String, Indexer> indexers) {
		MemBean.indexers = indexers;
	}
String mappername;
public String getMappername() {
	return mappername;
}

public void updateSettings()
{
int z=0;
z++;
}
public void setMappername(String mappername) {
	this.mappername = mappername;
}
	
	Integer mindf=5;
	Integer minfreq=5;
	public Integer getMindf() {
		return mindf;
	}
	public void setMindf(Integer mindf) {
		this.mindf = mindf;
	}
	public Integer getMinfreq() {
		return minfreq;
	}
	public void setMinfreq(Integer minfreq) {
		this.minfreq = minfreq;
	}
	
	List<Result> results = new ArrayList<Result>();
	Result[] arr = new Result[0];

	String sx[] = "1,2,3,4,5,5,3,3,2,1,1,43,4,54,4".split(",");
	List<String> s = Arrays.asList("1,2,3,4,5,5,3,3,2,1,1,43,4,54,4".split(","));

	public String[] getSx() {
		return sx;
	}

	public void setSx(String[] sx) {
		this.sx = sx;
	}

	public List<String> getS() {
		return s;
	}

	public void setS(List<String> s) {
		this.s = s;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Result[] getArr() {
		return arr;
	}

	public void setArr(Result[] arr) {
		this.arr = arr;
	}

	public void map() {
		Indexer idx = indexers.get(mappername);
		
		if (idx == null) {
			idx = new Indexer(MEMOMAPPING.ENGLISH1);

			InputStream loader = getClass().getClassLoader()
					.getResourceAsStream("de/l3s/sz/memorizer/models/mapper_standard_reuters.ser");

			try {
				idx.read(loader);
				
				indexers.put("English (Reuters Corpora)", idx);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		idx.filterModel(mindf, minfreq);
		
		results.clear();
		int cnt = 0;
		Iterator<Result> it = idx.result(number, false);
		while (it.hasNext()) {
			results.add(it.next());
			cnt++;
			if (cnt > 50)
				break;
		}

		arr = new Result[results.size()];
		results.toArray(arr);
	}

	private Indexer loadIndexer(String mappings,String modelname) {
		Indexer cidx = new Indexer();

		InputStream loader = getClass().getClassLoader()
				.getResourceAsStream("de/l3s/sz/memorizer/models/"+modelname);

		try {
			cidx.read(loader);
			return cidx;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public MemBean() {
		if(indexers==null)
		{
			indexers=new HashMap<>();
			
			indexers.put(mappername="English (Reuters Corpora)", loadIndexer(MEMOMAPPING.ENGLISH1,"mapper_standard_reuters.ser"));
			indexers.put("Russian (Lib.ru Corpora)", loadIndexer(MEMOMAPPING.RUSSIAN1,"mapper_standard_libru.ser"));
		}
		
	}

	public String getMessage() {
		return "I'm alive!";
	}
}