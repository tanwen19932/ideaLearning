package filter;

import java.util.List;

public  abstract class FilterChain<T> {
	protected List<Filter<T>> filters ;

	public List<Filter<T>> getFilter() {
		return filters;
	}

	public void setFilter(List<Filter<T>> filters) {
		this.filters = filters;
	}
	public void add(Filter<T> filter) {
		filters.add(filter);
	}
	public void remove(Filter<T> filter){
		if(filters.contains(filter)){
			filters.remove(filter);
		}
	}
	  
}
