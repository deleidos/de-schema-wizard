package com.deleidos.dp.deserializors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.deleidos.dp.beans.AliasNameDetails;
import com.deleidos.dp.beans.BinaryDetail;
import com.deleidos.dp.beans.Detail;
import com.deleidos.dp.beans.Interpretation;
import com.deleidos.dp.beans.Interpretations;
import com.deleidos.dp.beans.MatchingField;
import com.deleidos.dp.beans.NumberDetail;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.StringDetail;
import com.deleidos.dp.interpretation.builtin.AbstractBuiltinInterpretation;
import com.deleidos.hd.enums.MainType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Deserializer for profiles.  This class should be added as a module in the ObjectMapper.
 * @author leegc
 *
 */
public class ProfileDeserializer extends JsonDeserializer<Profile> {
	private static final Logger logger = Logger.getLogger(ProfileDeserializer.class);
	
	@Override
	public Profile deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Profile profile = new Profile();

		JsonNode rootNode = p.readValueAsTree();
		MainType mainType = MainType.fromString(rootNode.path("main-type").asText());
		
		if (mainType.equals(MainType.OBJECT)) {
			return Profile.objectProfile();
		}
		
		JsonNode detailJson = rootNode.path("detail");
		Detail detail;
		float presence = (float)rootNode.path("presence").asDouble(0.0);
		if(presence >= 0) {
			switch(mainType) {
			case NUMBER: {
				detail = SerializationUtility.deserialize(detailJson, NumberDetail.class);
				break;
			}
			case STRING: {
				detail = SerializationUtility.deserialize(detailJson, StringDetail.class);
				break;
			}
			case BINARY: {
				detail = SerializationUtility.deserialize(detailJson, BinaryDetail.class);
				break;
			} default: {
				return null;
			}
			}

			profile.setDetail(detail);

			List<AliasNameDetails> aliasNameList = null;
			JsonNode aliasNode = rootNode.path("aliasNames");
			if(aliasNode.size() > 0) {
				aliasNameList = new ArrayList<AliasNameDetails>();
				for(int i = 0; i < aliasNode.size(); i++) {
					aliasNameList.add(SerializationUtility.deserialize(aliasNode.get(i), AliasNameDetails.class));
				}
			}
			profile.setAliasNames(aliasNameList);

			List<MatchingField> matchingFieldList = null;
			JsonNode matchingFieldNode = rootNode.path("matching-fields");
			if(matchingFieldNode.size() > 0) {
				matchingFieldList = new ArrayList<MatchingField>();
				for(int i = 0; i < matchingFieldNode.size(); i++) {
					matchingFieldList.add(SerializationUtility.deserialize(matchingFieldNode.get(i), MatchingField.class));
				}
			}
			profile.setMatchingFields(matchingFieldList);
			/*if(!interpretations.isMissingNode()) {
				profile.setInterpretations(SerializationUtility.deserialize(interpretations
						, new TypeReference<List<Interpretation>>(){}));
				if(profile.getInterpretations() != null && !profile.getInterpretations().isEmpty()) {
					profile.setInterpretation(profile.getInterpretations().get(0));
				} else {
					profile.setInterpretation(SerializationUtility.deserialize(rootNode.path("interpretation"), Interpretation.class));
				}
			} else {
				profile.setInterpretation(SerializationUtility.deserialize(rootNode.path("interpretation"), Interpretation.class));
			}*/
			JsonNode interpretations = rootNode.path("interpretations");
			profile.setInterpretations(
					SerializationUtility.deserialize(interpretations, Interpretations.class));
			profile.setMainType(mainType.toString());
			profile.setMergedInto(rootNode.path("merged-into-schema").asBoolean());
			profile.setOriginalName(rootNode.path("original-name").asText(null));
			profile.setUsedInSchema(rootNode.path("used-in-schema").asBoolean());
			profile.setPresence((float)rootNode.path("presence").asDouble());
			profile.setDisplayName(rootNode.path("display-name").asText(null));

		} else {
			switch(mainType) {
			case NUMBER: {
				detail = new NumberDetail();
				break;
			}
			case STRING: {
				detail = new StringDetail();
				break;
			}
			case BINARY: {
				detail = new BinaryDetail();
				break;
			} default: {
				return null;
			}
			}
			detail.setDetailType(detailJson.path("detail-type").asText(null));
			profile.setMainType(mainType.toString());
			profile.setPresence(presence);
			profile.setDetail(detail);
		}
		return profile;

	}
}
