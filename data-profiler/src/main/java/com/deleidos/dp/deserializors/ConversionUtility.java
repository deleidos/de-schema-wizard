package com.deleidos.dp.deserializors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.StructuredNode;
import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.deleidos.dp.profiler.DisplayNameHelper;
import com.deleidos.hd.enums.MainType;

/**
 * Convert unstructured profiles to structured profiles.
 * @author leegc
 *
 */
public class ConversionUtility {
	public static final int LEAF_NODES_FIRST = 1;
	public static final int TREE_NODES_FIRST = -1;
	public static final int SORT_STRATEGY = LEAF_NODES_FIRST;

	/*public static Map<String, Profile> convertToFlattenedMap(List<StructuredNode> heirarchicalProfiles) {
		Map<String, Profile> flattendMap = new HashMap<String, Profile>();
		heirarchicalProfiles.forEach(profile->
			flattendMap.putAll(extractRootProfileToMap(profile, false)));
		return flattendMap;
	}

	private static Map<String, Profile> extractRootProfileToMap(StructuredNode profile, boolean includeObjectProfiles) {
		Map<String, Profile> profileMap = new HashMap<String, Profile>();
		final String rootPath = profile.getField();
		if(profile.getChildren().size() <= 0) {
			profileMap.put(profile.getField(), profile);
		} else {
			profile.getChildren().forEach(child->
				profileMap.putAll(recursivelyExtractProfilesToMap(
						child, rootPath, includeObjectProfiles))); 
			if(includeObjectProfiles) {
				profileMap.put(profile.getField(), profile);
			}
		}
		return profileMap;
	}

	private static Map<String, Profile> recursivelyExtractProfilesToMap(StructuredNode profile, String currentPath, boolean includeObjectProfiles) {
		Map<String, Profile> profileMap = new HashMap<String, Profile>();
		final String concatenatedPath = currentPath+DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER+profile.getField();
		if(profile.getChildren().size() <= 0) {
			profileMap.put(concatenatedPath, profile);
		} else {
			profile.getChildren().forEach(child->{
				profileMap.putAll(recursivelyExtractProfilesToMap(child, concatenatedPath, includeObjectProfiles));
			});
			profile.setChildren(Arrays.asList());
			if(includeObjectProfiles) {
				profileMap.put(concatenatedPath, profile);
			}
		}
		return profileMap;
	}*/

	@Deprecated
	public static Map<String, Profile> oldAddObjectProfiles(Map<String, Profile> flattenedMap, boolean setDisplayNames) {
		Map<String, Profile> objects = new HashMap<String, Profile>();
		flattenedMap.keySet().forEach(key-> {
			String[] splits = key.split("\\"+DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER);
			for(int i = 0; i < splits.length; i++) {
				StringBuilder concatenatedKey = new StringBuilder();
				concatenatedKey.append(splits[0]);
				objects.put(concatenatedKey.toString(), Profile.objectProfile());
				for(int j = 1; j < i; j++){
					concatenatedKey.append(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER+splits[j]);
					objects.put(concatenatedKey.toString(), Profile.objectProfile());
				}
			}
		});
		objects.forEach((k,v)->flattenedMap.putIfAbsent(k, v));
		return setDisplayNames ? DisplayNameHelper.determineDisplayNames(flattenedMap) : flattenedMap;
	}

	public static Map<String, Profile> addObjectProfiles(Map<String, Profile> flattenedMap, boolean setDisplayNames) {
		final Set<String> objectKeySet = flattenedMap.keySet().stream()
			.map(ConversionUtility::generateParentKeys)
			.flatMap(List::stream)
			.filter(key->!flattenedMap.containsKey(key))
			.collect(Collectors.toSet());
		objectKeySet.forEach(key->flattenedMap.put(key, Profile.objectProfile()));
		return setDisplayNames ? DisplayNameHelper.determineDisplayNames(flattenedMap) : flattenedMap;
	}

	public static List<String> generateParentKeys(String key) {
		String[] splits = key.split("\\"+DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER);
		return IntStream.range(0, splits.length)
			.mapToObj(index->generateParentKey(splits, index))
			.collect(Collectors.toList());
	}
	
	public static String generateParentKey(String[] keys, int endIndex) {
		return String.join(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER,
				Arrays.asList(keys).stream().limit(endIndex+1).collect(Collectors.toList()));
	}

	public static Map<String, Profile> addObjectProfiles(Map<String, Profile> flattenedMap) {
		return addObjectProfiles(flattenedMap, true);
	}

	private static StructuredNode recursivelyAddProfilesToProfile(
			Map<String, Profile> flattenedMap, String currentKey, final Incrementor inc) {
		String[] splits = currentKey.split("\\"+DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER);
		String fieldName = splits[splits.length-1];
		final StructuredNode node = new StructuredNode(currentKey, fieldName, inc.get());
		List<String> childrenKeys = getChildrenKeys(flattenedMap, currentKey);
		if(!childrenKeys.isEmpty()) {
			for(String child : childrenKeys) {
				node.getChildren().add( 
						recursivelyAddProfilesToProfile(flattenedMap, child, inc));
			}
		}
		return node;
	}

	@Deprecated
	public static List<String> oldGetRootKeys(Map<String, Profile> flattenedMap) {
		final List<String> children = new ArrayList<String>();
		flattenedMap.keySet().forEach(potentialChild -> {
			if(StringUtils.countMatches(potentialChild,  DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER) == 0) {
				children.add(potentialChild);
			}
		});
		return sortChildren(flattenedMap, children, SORT_STRATEGY);
	}
	
	public static List<String> getRootKeys(Map<String, Profile> flattenedMap) {
		return sortChildren(
				flattenedMap, 
				flattenedMap.keySet().stream()
					.filter(ROOT_LEVEL_KEYS_PREDICATE)
					.collect(Collectors.toList()),	
				SORT_STRATEGY);
	}
	
	public static KeysOnLevelPredicate ROOT_LEVEL_KEYS_PREDICATE = new KeysOnLevelPredicate(0);
	private static class KeysOnLevelPredicate implements Predicate<String> {
		final int level;
		
		public KeysOnLevelPredicate(int level) {
			this.level = level;
		}

		@Override
		public boolean test(String t) {
			return StringUtils.countMatches(t, DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER) == level;
		}
		
	}
	
	private static List<String> getChildrenKeys(final Map<String, Profile> flattenedMap, String key) {
		final String SOA = DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER;
		final int depth =  StringUtils.countMatches(key, SOA);
		return sortChildren(
				flattenedMap, 
				flattenedMap.keySet().stream()
					.filter(potentialChild -> potentialChild.startsWith(key))
					.filter(new KeysOnLevelPredicate(depth + 1))
					.collect(Collectors.toList()
				), 
				SORT_STRATEGY);
	}

	@Deprecated
	private static List<String> oldGetChildrenKeys(final Map<String, Profile> flattenedMap, String key) {
		final List<String> children = new ArrayList<String>();
		final int depth =  StringUtils.countMatches(key,  DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER);
		flattenedMap.keySet().forEach(potentialChild -> {
			if(StringUtils.countMatches(potentialChild,  
					DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER) == depth + 1 && 
					potentialChild.startsWith(key)) {
				children.add(potentialChild);
			}
		});
		return sortChildren(flattenedMap, children, SORT_STRATEGY);
	}

	@Deprecated
	public static List<String> oldSortChildren(Map<String, Profile> originalMap, List<String> children, int sortingStrategy) {
		children.sort((String c1, String c2)-> {
			if(originalMap.get(c1).getMainTypeClass().equals(MainType.OBJECT)) {
				return sortingStrategy;
			} else if(originalMap.get(c2).getMainTypeClass().equals(MainType.OBJECT)) {
				return -sortingStrategy;
			} 
			return 0;
		});
		return children;
	}
	
	public static List<String> sortChildren(Map<String, Profile> originalMap, List<String> children, int sortingStrategy) {
		children.sort(new ObjectComparator(originalMap, sortingStrategy));
		return children;
	}
	
	public static class ObjectComparator implements Comparator<String> {
		final Map<String, Profile> map;
		final int sortingStrategy;
		
		public ObjectComparator(Map<String, Profile> map, int sortingStrategy) {
			this.map = map;
			this.sortingStrategy = sortingStrategy;
		}

		@Override
		public int compare(String o1, String o2) {
			if(map.get(o1).getMainTypeClass().equals(MainType.OBJECT)) {
				return sortingStrategy;
			} else if(map.get(o2).getMainTypeClass().equals(MainType.OBJECT)) {
				return -sortingStrategy;
			} 
			return 0;
		}
		
	}

	public static List<StructuredNode> convertToHeirarchicalList(Map<String, Profile> flattenedProfiles) {
		final List<StructuredNode> heirarchicalList = new ArrayList<StructuredNode>();
		final Map<String, Profile> copyOfFlattenedProfiles = new HashMap<String, Profile>(flattenedProfiles); 
		copyOfFlattenedProfiles.putAll(addObjectProfiles(copyOfFlattenedProfiles));
		final Incrementor inc = new Incrementor(1);
		final List<String> rootObjects = getRootKeys(copyOfFlattenedProfiles);
		rootObjects.forEach(rootObject-> 
		heirarchicalList.add(recursivelyAddProfilesToProfile(copyOfFlattenedProfiles, rootObject, inc)));
		return heirarchicalList;
	}

	private static class Incrementor {
		int i;
		public Incrementor(int i) {this.i = i;}
		protected int get() { return i++; };
	}
}
