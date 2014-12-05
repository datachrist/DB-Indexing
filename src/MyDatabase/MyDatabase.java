package MyDatabase;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * @author 
 *
 */
public class MyDatabase {

	static final String FILEPATH = "us-500.csv";
	static MapStringList<Integer, Long> idMap = new MapStringList<Integer, Long>();
	static MapStringList<String, Long> lnameMap = new MapStringList<String, Long>();
	static MapStringList<String, Long> stateMap = new MapStringList<String, Long>();

	public static void main(String[] args) {

		try {

			String line;
			int count = 0;
			List<Long> offsetList = null;
			File df = new File("data.db");
			RandomAccessFile csvFile = new RandomAccessFile(FILEPATH, "r");
			RandomAccessFile dbfile = null;
			RandomAccessFile idIdxFile = new RandomAccessFile("id.ndx", "rw");
			RandomAccessFile lnameIdxFile = new RandomAccessFile("last_name.ndx", "rw");
			RandomAccessFile stateIdxFile = new RandomAccessFile("state.ndx", "rw");

			if (!df.exists()) {
				dbfile = new RandomAccessFile("data.db", "rw");
				csvFile.readLine();
				while ((line = csvFile.readLine()) != null) {
					String[] strArray = line.split("\",");

					// offset
					long offset = dbfile.getFilePointer();

					// dumbing into data.db file
					dbfile.writeBytes(line + "\n");

					// indexing based on id
					idMap.add(Integer.parseInt(strArray[0].substring(1)), (offset));
					// System.out.println( strArray[0]+ (offset+1));

					// indexing based on Last_name
					lnameMap.add(strArray[2].substring(1).toLowerCase(), (offset));
					// System.out.println( strArray[2]+ (offset+1));

					// indexing based on state
					stateMap.add(strArray[7].substring(1).toLowerCase(), (offset));
					// System.out.println( strArray[7]+ (offset+1));

				}
				csvFile.close();

				// writing maps into file
				for (java.util.Map.Entry<Integer, Long> entry : idMap.map.entrySet()) {
					idIdxFile.writeBytes(entry.getKey() + "," + idMap.getValues(entry.getKey()) + "\n");

				}
				idIdxFile.close();
				// writing maps into file
				for (java.util.Map.Entry<String, Long> entry : lnameMap.map.entrySet()) {
					lnameIdxFile.writeBytes(entry.getKey() + "," + lnameMap.getValues(entry.getKey()) + "\n");

				}
				lnameIdxFile.close();

				// writing maps into file
				for (java.util.Map.Entry<String, Long> entry : stateMap.map.entrySet()) {
					stateIdxFile.writeBytes(entry.getKey() + "," + stateMap.getValues(entry.getKey()) + "\n");

				}
				stateIdxFile.close();

			}

			else {
				idMap = new MapStringList<Integer, Long>();
				lnameMap = new MapStringList<String, Long>();
				stateMap = new MapStringList<String, Long>();
				while ((line = idIdxFile.readLine()) != null) {
					String data[] = line.split(",");
					// System.out.println(line);
					if (data.length >= 2) {
						data[1] = data[1].replace("[", "");
						data[1] = data[1].replace("]", "");
						String value[] = data[1].split(",");
						for (String offsetValue : value) {
							Long offset = Long.parseLong(offsetValue.trim());
							idMap.add(Integer.parseInt(data[0]), offset);
						}
					}
				}
				while ((line = stateIdxFile.readLine()) != null) {
					if (line.split(",").length >= 2) {
					String key = line.substring(0, line.indexOf(","));
					String values = line.substring(line.indexOf(",") + 1);
					/*
					 * System.out.println(key); System.out.println(values);
					 */
					String data[] = new String[2];
					data[0] = key;
					data[1] = values;
					data[1] = data[1].replace("[", "");
					data[1] = data[1].replace("]", "");
					String value[] = data[1].split(",");
					/*
					 * System.out.println(line); System.out.println(data[1]);
					 * System.out.println(value);
					 */
					for (String offsetValue : value) {
						Long offset = Long.parseLong(offsetValue.trim());
						stateMap.add(data[0], offset);
					}
					}
				}
				while ((line = lnameIdxFile.readLine()) != null) {
					if (line.split(",").length >= 2) {
					String key = line.substring(0, line.indexOf(","));
					String values = line.substring(line.indexOf(",") + 1);
					/*
					 * System.out.println(key); System.out.println(values);
					 */
					String data[] = new String[2];
					data[0] = key;
					data[1] = values;
					data[1] = data[1].replace("[", "");
					data[1] = data[1].replace("]", "");
					String value[] = data[1].split(",");
					for (String offsetValue : value) {
						Long offset = Long.parseLong(offsetValue.trim());
						lnameMap.add(data[0], offset);
					}
					}
				}
				idIdxFile.close();
				lnameIdxFile.close();
				stateIdxFile.close();

			}

			if (args.length == 0) {
				System.out.println("Please provide correct operations<insert, delete, count, modify> as an argument");
				return;
			}

			switch (args[0]) {

			case "select":
				String idxType = args[1];
				String fieldName = args[2];
				System.out.println("Record(s)");
				if (idxType.equalsIgnoreCase("Id")) {
					offsetList = idMap.getValues(Integer.parseInt(fieldName));

					String[] recordList = new String[offsetList.size()];
					for (int i = 0; i < offsetList.size(); i++) {
						recordList[i] = readFromFile("data.db", offsetList.get(i), 1024);
						System.out.println(recordList[i]);

					}
				} else if (idxType.equalsIgnoreCase("last_name")) {
					offsetList = lnameMap.getValues(fieldName);

					String[] recordList = new String[offsetList.size()];
					for (int i = 0; i < offsetList.size(); i++) {
						recordList[i] = readFromFile("data.db", offsetList.get(i), 1024);
						System.out.println(recordList[i]);
					}
				} else if (idxType.equalsIgnoreCase("state")) {
					offsetList = stateMap.getValues(fieldName);

					String[] recordList = new String[offsetList.size()];
					for (int i = 0; i < offsetList.size(); i++) {
						recordList[i] = readFromFile("data.db", offsetList.get(i), 1024);
						System.out.println(recordList[i]);
					}
				}
				break;
			case "delete":
				int id = Integer.parseInt(args[1]);

				offsetList = idMap.getValues(id);
				if(offsetList==null){
					System.out.println("Record Not Found");
					return;
				}
				// reading record from db
				String record1 = readFromFile("data.db", offsetList.get(0), 1024);
				Employee emp1 = new Employee();
				emp1 = emp1.convertToObject(record1, "\",");

				String[] recordList = new String[offsetList.size()];
				for (int i = 0; i < offsetList.size(); i++) {

					String blankStr = new String();
					deleteFromFile("data.db", offsetList.get(i));
					// writeToFile("data.db",zeroStr, offsetList.get(i));

				}
				// deleting id index
				idMap.map.remove(id);

				// deleting state index
				if (emp1.getState() != null) {
					List<Long> newOffsetList = stateMap.getValues(emp1.getState().toLowerCase());
					newOffsetList.remove(offsetList.get(0));
					if (stateMap.getValues(emp1.getState().toLowerCase()).isEmpty()) {
						stateMap.map.remove(emp1.getState().toLowerCase());
					}
				}
				if (emp1.getLastName() != null) {
					List<Long> newOffsetList = lnameMap.getValues(emp1.getLastName().toLowerCase());
					newOffsetList.remove(offsetList.get(0));
					if (lnameMap.getValues(emp1.getLastName().toLowerCase()).isEmpty()) {
						lnameMap.map.remove(emp1.getLastName().toLowerCase());
					}

				}

				System.out.println("Deleted!");
				break;
			case "insert":
				String strInput = args[1];
				String[] arr = strInput.split("',");
				dbfile = new RandomAccessFile("data.db", "rw");
				long offsetnew = dbfile.length();
				if (idMap.map.containsKey(Integer.parseInt(arr[0].substring(1)))) {
					System.out.println("cannot be inserted duplicate primary key");
					return;
				} else {
					Employee emp2 = new Employee();
					emp2 = emp2.convertToObject(strInput, "',");
					String recordData = emp2.ConvertToRecord(emp2);
					writeToFile("data.db", recordData, offsetnew);
					idMap.add(Integer.parseInt(arr[0].substring(1)), offsetnew + 1);
					stateMap.add(arr[2], offsetnew + 1);
					lnameMap.add(arr[7], offsetnew + 1);
				}
				dbfile.close();
				System.out.println("Inserted");
				break;

			case "modify":
				String field_name = args[2];
				String field_value = args[3];

				if (!idMap.map.containsKey(Integer.parseInt(args[1]))) {
					System.out.println("cannot be updated. No Record found!");
				} else {
					offsetList = idMap.getValues(Integer.parseInt(args[1]));

					String record = readFromFile("data.db", offsetList.get(0), 1024);
					Employee emp = new Employee();
					emp = emp.convertToObject(record, "\",");
					//System.out.println(record.length());
					String oldlname = null, oldstate = null;

					switch (field_name.toLowerCase()) {
					case "id":
						System.out.println("Cannot modify ID");
						break;
					case "first_name":
						emp.setFirstName(field_value);
						break;
					case "last_name":
						oldlname = emp.getLastName().toString();
						emp.setLastName(field_value);
						break;
					case "company_name":
						emp.setCompanyName(field_value);
						break;
					case "address":
						emp.setAddress(field_value);
						break;
					case "city":
						emp.setCity(field_value);
						break;
					case "county":
						emp.setCounty(field_value);
						break;
					case "state":
						oldstate = emp.getState().toString();
						emp.setState(field_value);
						break;
					case "zip":
						emp.setZipCode(Integer.parseInt(field_value));
						break;
					case "phone1":
						emp.setPhone1(field_value);
						break;
					case "phone2":
						emp.setPhone2(field_value);
						break;
					case "email":
						emp.setEmail(field_value);
						break;
					case "web":
						emp.setWeb(field_value);
						break;
					}

					// writing into file
					String recordData = emp.ConvertToRecord(emp);
					modifyFromFile("data.db", recordData, offsetList.get(0));

					List<Long> newOffsetList = null;
					if (oldlname != null) {
						newOffsetList = lnameMap.getValues(oldlname.toLowerCase());
						newOffsetList.remove(offsetList.get(0));
						lnameMap.add(emp.getLastName(), offsetList.get(0));
						if (lnameMap.getValues(oldlname.toLowerCase()).isEmpty()) {
							lnameMap.map.remove(oldlname.toLowerCase());
						}
					} else if (oldstate != null) {
						newOffsetList = stateMap.getValues(oldstate.toLowerCase());
						newOffsetList.remove(offsetList.get(0));
						stateMap.add(emp.getState(), offsetList.get(0));
						if (stateMap.getValues(oldstate.toLowerCase()).isEmpty()) {
							stateMap.map.remove(oldstate.toLowerCase());
						}
					}

					System.out.println("Modified!");

				}

				break;
			case "count":
				System.out.println("The Total Number of records is " + idMap.map.size());

				return;
			default:
				System.out.println("Provide correct operations<insert, delete, count, modify>");
				return;
			}

			File idlf = new File("id.ndx");
			if (!idlf.exists()) {
				idlf.delete();
			}
			File dlfl = new File("last_name.ndx");
			if (!dlfl.exists()) {
				dlfl.delete();
			}
			File dsfl = new File("state.ndx");
			if (!dsfl.exists()) {
				dsfl.delete();
			}
			// writing maps into file
			RandomAccessFile idIdxFile1 = new RandomAccessFile("id.ndx", "rwd");
			RandomAccessFile lnameIdxFile1 = new RandomAccessFile("last_name.ndx", "rwd");
			RandomAccessFile stateIdxFile1 = new RandomAccessFile("state.ndx", "rwd");
			for (java.util.Map.Entry<Integer, Long> entry : idMap.map.entrySet()) {
				idIdxFile1.writeBytes(entry.getKey() + "," + idMap.getValues(entry.getKey()) + "\n");

			}
			idIdxFile1.close();
			// writing maps into file
			for (java.util.Map.Entry<String, Long> entry : lnameMap.map.entrySet()) {
				lnameIdxFile1.writeBytes(entry.getKey() + "," + lnameMap.getValues(entry.getKey()) + "\n");

			}
			lnameIdxFile1.close();

			// writing maps into file
			for (java.util.Map.Entry<String, Long> entry : stateMap.map.entrySet()) {
				stateIdxFile1.writeBytes(entry.getKey() + "," + stateMap.getValues(entry.getKey()) + "\n");

			}
			stateIdxFile1.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String readFromFile(String filePath, Long position, int size) throws IOException {
		String str = "";
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		file.seek(position);
		char b = (char) file.read();
		while (b != '\n') {

			str += b;
			b = (char) file.read();
		}
		file.close();
		return str;

	}

	private static void writeToFile(String filePath, String data, Long position) throws IOException {

		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.seek(position);
		file.write(data.getBytes());
		file.close();

	}

	private static void deleteFromFile(String filePath, Long position) throws IOException {

		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.seek(position);
		String data = "0";
		char b = (char) file.read();
		file.seek(file.getFilePointer() - 1);
		while (b != '\n') {

			file.write(data.getBytes());
			b = (char) file.read();
			file.seek(file.getFilePointer() - 1);

		}

		file.close();

	}

	private static void modifyFromFile(String filePath, String data, Long position) throws IOException {
		//System.out.println(data.length());
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.seek(position);
		byte[] bytes = data.getBytes();
		char b = (char) file.read();
		file.seek(file.getFilePointer() - 1);
		int i = 0;
		String blank = " ";
		while (b != '\n') {
			if (i >= bytes.length)
				file.write(blank.getBytes());
			else
				file.write(bytes[i]);
			b = (char) file.read();
			file.seek(file.getFilePointer() - 1);
			i++;
		}

		file.close();

	}

	private static void createMaps() {
		// TODO Auto-generated method stub
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class Employee {
	private Integer id;
	private String firstName;
	private String lastName;
	private String companyName;
	private String address;
	private String city;
	private String county;
	private String state;
	private int zipCode;
	private String phone1;
	private String phone2;
	private String email;
	private String web;

	

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName
	 *            the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}

	/**
	 * @param county
	 *            the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zipCode
	 */
	public int getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode
	 *            the zipCode to set
	 */
	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the phone1
	 */
	public String getPhone1() {
		return phone1;
	}

	/**
	 * @param phone1
	 *            the phone1 to set
	 */
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	/**
	 * @return the phone2
	 */
	public String getPhone2() {
		return phone2;
	}

	/**
	 * @param phone2
	 *            the phone2 to set
	 */
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the web
	 */
	public String getWeb() {
		return web;
	}

	/**
	 * @param web
	 *            the web to set
	 */
	public void setWeb(String web) {
		this.web = web;
	}

	public Employee convertToObject(String record, String Splitter) {
		Employee employee = new Employee();
		String[] strArray = record.split(Splitter);

		employee.setId(Integer.parseInt(strArray[0].substring(1)));
		employee.setFirstName(strArray[1].substring(1));
		employee.setLastName(strArray[2].substring(1));
		employee.setCompanyName(strArray[3].substring(1));
		employee.setAddress(strArray[4].substring(1));
		employee.setCity(strArray[5].substring(1));
		employee.setCounty(strArray[6].substring(1));
		employee.setState(strArray[7].substring(1));
		if (strArray[7].contains("'")) {
			employee.setZipCode(Integer.parseInt(strArray[8].substring(1)));
			employee.setPhone1(strArray[9].substring(1));
			employee.setPhone2(strArray[10].substring(1));
			employee.setEmail(strArray[11].substring(1));
			employee.setWeb(strArray[12].substring(1, strArray[12].length() - 1));
		} else {
			employee.setZipCode(Integer.parseInt(strArray[8].substring(0, 5)));
			employee.setPhone1(strArray[8].substring(7));

			employee.setPhone2(strArray[9].substring(1));
			employee.setEmail(strArray[10].substring(1));
			employee.setWeb(strArray[11].substring(1, strArray[11].length() - 1));
		}
		return employee;
	}

	public String ConvertToRecord(Employee emp) {
		String record = "\"" + emp.getId() + "\",\"" + emp.getFirstName() + "\",\"" + emp.getLastName() + "\",\""
				+ emp.getCompanyName() + "\",\"" + emp.getAddress() + "\",\"" + emp.getCity() + "\",\""
				+ emp.getCounty() + "\",\"" + emp.getState() + "\"," + emp.getZipCode() + ",\"" + emp.getPhone1()
				+ "\",\"" + emp.getPhone2() + "\",\"" + emp.getEmail() + "\",\"" + emp.getWeb() + "\"";

		return record;
	}

}

class MapStringList<K, V> extends HashMap<K, V> {
	public TreeMap<K, V> map;

	public MapStringList() {
		map = new TreeMap<K, V>();
	}

	public void add(K key, V value) {
		if (map.containsKey(key)) {
			List<V> values = (List<V>) map.get(key);
			values.add(value);
		}

		else {
			List<V> values = new ArrayList();
			values.add(value);
			map.put(key, (V) values);
		}
	}

	public List<V> getValues(K key) {
		return (List) map.get(key);
	}

}
