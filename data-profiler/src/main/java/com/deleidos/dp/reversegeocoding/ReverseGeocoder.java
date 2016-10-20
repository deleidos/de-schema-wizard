package com.deleidos.dp.reversegeocoding;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.interpretation.HttpInterpretationEngine;
import com.deleidos.dp.interpretation.InterpretationEngineFacade;

/**
 * Class that passes requests for reverse geocoding to the Interpretation Engine.  Use a ReverseGeocoderCallbackListener
 * to receive the results of the request.
 * @author leegc
 *
 */
public class ReverseGeocoder {
	private static final Logger logger = Logger.getLogger(ReverseGeocoder.class);
	private ReverseGeocodingWorker reverseGeocodingWorker;
	private ReverseGeocoderCallbackListener callbackListener = null;

	public ReverseGeocoder() throws DataAccessException {
		reverseGeocodingWorker = InterpretationEngineFacade.getInstance();
	}

	public interface ReverseGeocodingWorker {
		public List<String> getCountryCodesFromCoordinateList(List<Double[]> latlngs) throws DataAccessException;
	}
	
	public interface ReverseGeocoderCallbackListener {
		public void handleResult(int coordinateProfileIndex, List<String> resultingCountryNames);
	}
	
	public void setCallbackListener(ReverseGeocoderCallbackListener listener) {
		this.callbackListener = listener;
	}
	
	public void getCountriesFromLatLngsASync(int coordinateProfileIndex, List<Double[]> latLngs) {
		if(latLngs.isEmpty()) {
			if(callbackListener != null) {
				synchronized(callbackListener) {
					callbackListener.handleResult(coordinateProfileIndex, new ArrayList<String>());
				}
			}
			return;
		}
		List<Double[]> latLngsCopy = new ArrayList<Double[]>(latLngs);
		Thread t = new Thread() {
			@Override
			public void run() {
				List<String> resultList;
				try {
					resultList = reverseGeocodingWorker.getCountryCodesFromCoordinateList(latLngsCopy);
				} catch (DataAccessException e) {
					logger.error("Error from the Python Interpretation Engine: " + e);
					resultList = new ArrayList<String>();
				}
				if(callbackListener != null) {
					// synchronized on callbackListener so other threads aren't changing values concurrently
					synchronized(callbackListener) {
						callbackListener.handleResult(coordinateProfileIndex, resultList);
					}
				}
			};
		};
		t.start();
	}
}
