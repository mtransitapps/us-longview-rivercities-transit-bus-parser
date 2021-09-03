package org.mtransit.parser.us_longview_rivercities_transit_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.mt.data.MAgency;

import java.util.regex.Pattern;

// http://data.trilliumtransit.com/gtfs/rivercitiestransit-wa-us/
// http://data.trilliumtransit.com/gtfs/rivercitiestransit-wa-us/rivercitiestransit-wa-us.zip
public class LongviewRiverCitiesTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new LongviewRiverCitiesTransitBusAgencyTools().start(args);
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "RiverCities Transit";
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		String rsnS = gRoute.getRouteShortName();
		if (rsnS.isEmpty()) {
			rsnS = gRoute.getRouteLongNameOrDefault();
		}
		if (rsnS.isEmpty() || !CharUtils.isDigitsOnly(rsnS)) {
			throw new MTLog.Fatal("Unexpected route ID for %s!", gRoute.toStringPlus());
		}
		return Long.parseLong(rsnS); // using route short name as route ID
	}

	@Nullable
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteShortName())) {
			return gRoute.getRouteLongNameOrDefault();
		}
		return super.getRouteShortName(gRoute);
	}

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteLongName())) {
			return "Route " + gRoute.getRouteShortName();
		}
		return super.getRouteLongName(gRoute);
	}

	private static final String AGENCY_COLOR_BLUE = "0E4878"; // BLUE (from web site CSS)
	// private static final String AGENCY_COLOR_BLUE = "1363A4"; // BLUE (from web site CSS)

	private static final String AGENCY_COLOR = AGENCY_COLOR_BLUE;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern LOOP_ROUTE_ = CleanUtils.cleanWord("loop route");
	private static final String LOOP_ROUTE_REPLACEMENT = CleanUtils.cleanWordsReplacement("Loop");

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = LOOP_ROUTE_.matcher(tripHeadsign).replaceAll(LOOP_ROUTE_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.cleanSlashes(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
