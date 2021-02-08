package org.mtransit.parser.us_longview_rivercities_transit_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MDirectionType;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// http://data.trilliumtransit.com/gtfs/rivercitiestransit-wa-us/
// http://data.trilliumtransit.com/gtfs/rivercitiestransit-wa-us/rivercitiestransit-wa-us.zip
public class LongviewRiverCitiesTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@Nullable String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/us-longview-rivercities-transit-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new LongviewRiverCitiesTransitBusAgencyTools().start(args);
	}

	@Nullable
	private HashSet<Integer> serviceIdInts;

	@Override
	public void start(@NotNull String[] args) {
		MTLog.log("Generating RiverCities Transit bus data...");
		long start = System.currentTimeMillis();
		this.serviceIdInts = extractUsefulServiceIdInts(args, this, true);
		super.start(args);
		MTLog.log("Generating RiverCities Transit bus data... DONE in %s.", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIdInts != null && this.serviceIdInts.isEmpty();
	}

	@Override
	public boolean excludeCalendar(@NotNull GCalendar gCalendar) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarInt(gCalendar, this.serviceIdInts);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(@NotNull GCalendarDate gCalendarDates) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarDateInt(gCalendarDates, this.serviceIdInts);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if (this.serviceIdInts != null) {
			return excludeUselessTripInt(gTrip, this.serviceIdInts);
		}
		return super.excludeTrip(gTrip);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		return Long.parseLong(gRoute.getRouteShortName()); // using route short name as route ID
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

	private static final String AMTRAK_GREYHOUND = "Amtrak / Greyhound";
	private static final String FRED_MEYER = "Fred Meyer";
	private static final String HERON = "Heron";
	private static final String HERON_POINTE = HERON + " Pte";
	private static final String KELSO_HIGH_SCHOOL = "Kelso HS";
	private static final String RA_LONG_HS = "RA Long HS";
	private static final String THREE_RIVERS_MALL = "Three Rivers Mall";
	private static final String TRANSIT_CENTER = "Transit Ctr";

	private static final HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;

	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<>();
		//noinspection deprecation
		map2.put(30L, new RouteTripSpec(30L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TRANSIT_CENTER, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, "Ocean Beach") //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(
								"770554", // Ocean Beach Hwy & 28th <=
								"770668", // ++
								"770744" // Transit Center
						)) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(
								"770744", // Transit Center
								"770634", // ++
								"770554" // Ocean Beach Hwy & 28th =>
						)) //
				.compileBothTripSort());
		//noinspection deprecation
		map2.put(31L, new RouteTripSpec(31L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TRANSIT_CENTER, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, RA_LONG_HS) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList("770656", "770666", "770744")) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList("770744", "770634", "770656")) //
				.compileBothTripSort());
		//noinspection deprecation
		map2.put(32L, new RouteTripSpec(32L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TRANSIT_CENTER, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, FRED_MEYER) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(
								"770700", // 30th  & Larch <=
								"770699", // 30th & Maryland <=
								"770707", // ++
								"770744" // Transit Center
						)) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(
								"770744", // Transit Center
								"770688", // ++
								"770699", // 30th & Maryland =>
								"770700" // 30th  & Larch =>
						)) //
				.compileBothTripSort());
		//noinspection deprecation
		map2.put(33L, new RouteTripSpec(33L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TRANSIT_CENTER, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, HERON_POINTE) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList("770561", "770552", "770555", "770744")) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList("770744", "770732", "770561")) //
				.compileBothTripSort());
		//noinspection deprecation
		map2.put(44L, new RouteTripSpec(44L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, AMTRAK_GREYHOUND, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, HERON) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList("770561", "770554", "770567")) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList("770567", "770573", "770561")) //
				.compileBothTripSort());
		//noinspection deprecation
		map2.put(45L, new RouteTripSpec(45L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, AMTRAK_GREYHOUND, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TRANSIT_CENTER) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList("770744", "770580", "770567")) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList("770567", "770585", "770744")) //
				.compileBothTripSort());
		//noinspection deprecation
		map2.put(56L, new RouteTripSpec(56L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, THREE_RIVERS_MALL, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, AMTRAK_GREYHOUND) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList("770567", "770655", "770568")) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList("770568", "770615", "770567")) //
				.compileBothTripSort());
		//noinspection deprecation
		map2.put(57L, new RouteTripSpec(57L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, AMTRAK_GREYHOUND, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, KELSO_HIGH_SCHOOL) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(
								"770636", // <=
								"770649", // ==
								"770592", // !=
								"770650", // !=
								"770651", // ==
								"770685", // Washington Way & 9th Ave
								"770567" // Amtrak/Greyhound
						)) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(
								"770567", // Amtrak/Greyhound
								"770632", // Mill  & 8th
								"770569", // ==
								"781805", // !=
								"808184", // !=
								"770636" // =>
						)) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, @NotNull List<MTripStop> list1, @NotNull List<MTripStop> list2, @NotNull MTripStop ts1, @NotNull MTripStop ts2, @NotNull GStop ts1GStop, @NotNull GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@NotNull
	@Override
	public ArrayList<MTrip> splitTrip(@NotNull MRoute mRoute, @Nullable GTrip gTrip, @NotNull GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@NotNull
	@Override
	public Pair<Long[], Integer[]> splitTripStop(@NotNull MRoute mRoute, @NotNull GTrip gTrip, @NotNull GTripStop gTripStop, @NotNull ArrayList<MTrip> splitTrips, @NotNull GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(@NotNull MRoute mRoute, @NotNull MTrip mTrip, @NotNull GTrip gTrip, @NotNull GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		mTrip.setHeadsignString(
				cleanTripHeadsign(gTrip.getTripHeadsignOrDefault()),
				gTrip.getDirectionId() == null ? 0 : gTrip.getDirectionId()
		);
	}

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		throw new MTLog.Fatal("Unexpected trips to merge: %s & %s!", mTrip, mTripToMerge);
	}

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
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
