package testStuff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.Namespace;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CsvManager {

	private static final Integer FISCAL_YEAR = 2018;

	private static final String CSV_DIRECTORY_PATH = "C:\\Eclipse\\testStuff\\csvs\\";
	private static final String XML_DIRECTORY_PATH = "C:\\Eclipse\\testStuff\\xmls\\";
	private static final String TEST_DIRECTORY_PATH = "C:\\Eclipse\\testStuff\\testData\\";

	private static BigInteger _numEntries = BigInteger.valueOf(0);
	private static BigDecimal _totalDebit = BigDecimal.valueOf(0);
	private static BigDecimal _totalCredit = BigDecimal.valueOf(0);

	public static void main(String[] args) throws Exception {
		test();
	}

	private static void test() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		
		Element AuditFile = doc.createElement("AuditFile");
		AuditFile.setAttribute("xmlns", "urn:OECD:StandardAuditFile-Tax:PT_1.04_01");
		AuditFile.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		AuditFile.setAttribute("xmlns:schemaLocation", "urn:OECD:StandardAuditFile-Tax:PT_1.04_01 .\\SAFTPT1.04_01.xsd");
		doc.appendChild(AuditFile);

		Element Header = doc.createElement("Header");
		AuditFile.appendChild(Header);

		Element MasterFiles = doc.createElement("MasterFiles");
		MasterFiles.setAttribute("xmlns", "urn:OECD:StandardAuditFile-Tax:PT_1.04_01");
		AuditFile.appendChild(MasterFiles);

		Element GeneralLedgerEntries = doc.createElement("GeneralLedgerEntries");
		GeneralLedgerEntries.setAttribute("xmlns", "urn:OECD:StandardAuditFile-Tax:PT_1.04_01");
		AuditFile.appendChild(GeneralLedgerEntries);

		generateHeaderXml(doc, Header);

		generateGlasXml(doc, MasterFiles);
		generateCustomersXml(doc, MasterFiles);
		generateSuppliersXml(doc, MasterFiles);
		generateProductsXml(doc, MasterFiles);
		generateTaxTableXml(doc, MasterFiles);

		generatePinXml(doc, GeneralLedgerEntries);
		generatePcrXml(doc, GeneralLedgerEntries);
		generateCshXml(doc, GeneralLedgerEntries);
		generateMonthlyXml(doc, GeneralLedgerEntries);
		generateDivXml(doc, GeneralLedgerEntries);
		generateIvaXml(doc, GeneralLedgerEntries);
		// generateEndXml(doc, GeneralLedgerEntries);

		Element NumberOfEntries = doc.createElement("NumberOfEntries");
		NumberOfEntries.appendChild(doc.createTextNode("" + _numEntries));

		Element TotalDebit = doc.createElement("TotalDebit");
		TotalDebit.appendChild(doc.createTextNode("" + _totalDebit));

		Element TotalCredit = doc.createElement("TotalCredit");
		TotalCredit.appendChild(doc.createTextNode("" + _totalCredit));

		GeneralLedgerEntries.insertBefore(TotalCredit, GeneralLedgerEntries.getFirstChild());
		GeneralLedgerEntries.insertBefore(TotalDebit, GeneralLedgerEntries.getFirstChild());
		GeneralLedgerEntries.insertBefore(NumberOfEntries, GeneralLedgerEntries.getFirstChild());

		storeXmlFile(doc, "try.xml");
	}

	private static void generateHeaderXml(Document doc, Element header) {
		Element AuditFileVersion = doc.createElement("AuditFileVersion");
		AuditFileVersion.appendChild(doc.createTextNode("1.04_01"));
		header.appendChild(AuditFileVersion);

		Element CompanyID = doc.createElement("CompanyID");
		CompanyID.appendChild(doc.createTextNode("SINTRA 513626930"));
		header.appendChild(CompanyID);

		Element TaxRegistrationNumber = doc.createElement("TaxRegistrationNumber");
		TaxRegistrationNumber.appendChild(doc.createTextNode("513626930"));
		header.appendChild(TaxRegistrationNumber);

		Element TaxAccountingBasis = doc.createElement("TaxAccountingBasis");
		TaxAccountingBasis.appendChild(doc.createTextNode("C"));
		header.appendChild(TaxAccountingBasis);

		Element CompanyName = doc.createElement("CompanyName");
		CompanyName.appendChild(doc.createTextNode("Candor Renting S.A."));
		header.appendChild(CompanyName);
		
		Element CompanyAddress = doc.createElement("CompanyAddress");
		header.appendChild(CompanyAddress);
		
		Element AddressDetail = doc.createElement("AddressDetail");
		AddressDetail.appendChild(doc.createTextNode("Rua do Mar da China, Nº1, 2.2, Parque das Nações"));
		CompanyAddress.appendChild(AddressDetail);
		
		Element City = doc.createElement("City");
		City.appendChild(doc.createTextNode("Lisboa"));
		CompanyAddress.appendChild(City);
		
		Element PostalCode = doc.createElement("PostalCode");
		PostalCode.appendChild(doc.createTextNode("1990-137"));
		CompanyAddress.appendChild(PostalCode);
		
		Element Country = doc.createElement("Country");
		Country.appendChild(doc.createTextNode("PT"));
		CompanyAddress.appendChild(Country);

		Element FiscalYear = doc.createElement("FiscalYear");
		FiscalYear.appendChild(doc.createTextNode("" + FISCAL_YEAR));
		header.appendChild(FiscalYear);

		Element StartDate = doc.createElement("StartDate");
		StartDate.appendChild(doc.createTextNode("" + FISCAL_YEAR + "-01-01"));
		header.appendChild(StartDate);

		Element EndDate = doc.createElement("EndDate");
		EndDate.appendChild(doc.createTextNode("" + FISCAL_YEAR + "-12-31"));
		header.appendChild(EndDate);

		Element CurrencyCode = doc.createElement("CurrencyCode");
		CurrencyCode.appendChild(doc.createTextNode("EUR"));
		header.appendChild(CurrencyCode);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime now = LocalDateTime.now();
		Element DateCreated = doc.createElement("DateCreated");
		DateCreated.appendChild(doc.createTextNode(dtf.format(now)));
		header.appendChild(DateCreated);

		Element TaxEntity = doc.createElement("TaxEntity");
		TaxEntity.appendChild(doc.createTextNode("Sede"));
		header.appendChild(TaxEntity);

		Element ProductCompanyTaxID = doc.createElement("ProductCompanyTaxID");
		ProductCompanyTaxID.appendChild(doc.createTextNode("513626930"));
		header.appendChild(ProductCompanyTaxID);

		Element SoftwareCertificateNumber = doc.createElement("SoftwareCertificateNumber");
		SoftwareCertificateNumber.appendChild(doc.createTextNode("0"));
		header.appendChild(SoftwareCertificateNumber);

		Element ProductID = doc.createElement("ProductID");
		ProductID.appendChild(doc.createTextNode("Candor_SAFT_Generator"));
		header.appendChild(ProductID);

		Element ProductVersion = doc.createElement("ProductVersion");
		ProductVersion.appendChild(doc.createTextNode("2018.01"));
		header.appendChild(ProductVersion);
		
		Element Website = doc.createElement("Website");
		Website.appendChild(doc.createTextNode("www.candor.pt"));
		header.appendChild(Website);
	}

	private static void generateEndXml(Document doc, Element generalLedgerEntries) throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "end.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();
		// "Id","NIF__c","Name","BillingStreet","BillingCity","BillingPostalCode","BillingCountryCode"
		for (CSVRecord csvRecord : csvParser) {
			// Accessing values by Header names
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		generateXmlFileEnd(doc, generalLedgerEntries, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "csh.xml");
	}

	private static void generateXmlFileEnd(Document doc, Element generalLedgerEntries,
			Map<String, TxValues> txToDetails, Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries,
			BigDecimal totalDebit, BigDecimal totalCredit) {

		String journalId = "END";
		String journalDescription = "Encerramento";
		String txType = "A";

		Element journal = doc.createElement("Journal");
		generalLedgerEntries.appendChild(journal);

		Element JournalID = doc.createElement("JournalID");
		JournalID.appendChild(doc.createTextNode(journalId));
		journal.appendChild(JournalID);

		Element jDescription = doc.createElement("Description");
		jDescription.appendChild(doc.createTextNode(journalDescription));
		journal.appendChild(jDescription);

		System.out.println("END");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void generateIvaXml(Document doc, Element generalLedgerEntries) throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "iva.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();
		// "Id","NIF__c","Name","BillingStreet","BillingCity","BillingPostalCode","BillingCountryCode"
		for (CSVRecord csvRecord : csvParser) {
			// Accessing values by Header names
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFileIva(doc, generalLedgerEntries, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "csh.xml");
	}

	private static void generateXmlFileIva(Document doc, Element generalLedgerEntries,
			Map<String, TxValues> txToDetails, Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries,
			BigDecimal totalDebit, BigDecimal totalCredit) {

		String journalId = "IVA";
		String journalDescription = "Apuramento de IVA";
		String txType = "R";

		Element journal = doc.createElement("Journal");
		generalLedgerEntries.appendChild(journal);

		Element JournalID = doc.createElement("JournalID");
		JournalID.appendChild(doc.createTextNode(journalId));
		journal.appendChild(JournalID);

		Element jDescription = doc.createElement("Description");
		jDescription.appendChild(doc.createTextNode(journalDescription));
		journal.appendChild(jDescription);

		System.out.println("IVA");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void generateDivXml(Document doc, Element generalLedgerEntries) throws Exception {
		String journalId = "DIV";
		String journalDescription = "Operações Diversas";

		Element journal = doc.createElement("Journal");
		generalLedgerEntries.appendChild(journal);

		Element JournalID = doc.createElement("JournalID");
		JournalID.appendChild(doc.createTextNode(journalId));
		journal.appendChild(JournalID);

		Element jDescription = doc.createElement("Description");
		jDescription.appendChild(doc.createTextNode(journalDescription));
		journal.appendChild(jDescription);

		generateCorrectionsXml(doc, journal);
	}

	private static void generateMonthlyXml(Document doc, Element generalLedgerEntries) throws Exception {
		String journalId = "MONTHLY";
		String journalDescription = "Operações Mensais";

		Element journal = doc.createElement("Journal");
		generalLedgerEntries.appendChild(journal);

		Element JournalID = doc.createElement("JournalID");
		JournalID.appendChild(doc.createTextNode(journalId));
		journal.appendChild(JournalID);

		Element jDescription = doc.createElement("Description");
		jDescription.appendChild(doc.createTextNode(journalDescription));
		journal.appendChild(jDescription);

		generateRentsXml(doc, journal);
		generateSalariesXml(doc, journal);
		generateAcrescimosXml(doc, journal);
		generateDepreciacoesXml(doc, journal);
	}

	private static void generateDepreciacoesXml(Document doc, Element journal) throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "deprec.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();

		for (CSVRecord csvRecord : csvParser) {
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFileDeprec(doc, journal, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "rents.xml");
	}

	private static void generateXmlFileDeprec(Document doc, Element journal, Map<String, TxValues> txToDetails,
			Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries, BigDecimal totalDebit,
			BigDecimal totalCredit) {
		
		String journalId = "MONTHLY";
		String txType = "N";

		System.out.println("Deprec");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void generateAcrescimosXml(Document doc, Element journal) throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "acresc.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();

		for (CSVRecord csvRecord : csvParser) {
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFileAcresc(doc, journal, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "rents.xml");
	}

	private static void generateXmlFileAcresc(Document doc, Element journal, Map<String, TxValues> txToDetails,
			Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries, BigDecimal totalDebit,
			BigDecimal totalCredit) {
		String journalId = "MONTHLY";
		String txType = "N";

		System.out.println("Acrescimos");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void writeToFile(String text) throws Exception {
		Files.write(Paths.get(TEST_DIRECTORY_PATH + "saft.xml"), text.getBytes(), StandardOpenOption.APPEND);
	}

	private static void generateSalariesXml(Document doc, Element journal) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "salaries.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();

		for (CSVRecord csvRecord : csvParser) {
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFileSalaries(doc, journal, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "rents.xml");
	}

	private static void generateXmlFileSalaries(Document doc, Element journal, Map<String, TxValues> txToDetails,
			Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries, BigDecimal totalDebit,
			BigDecimal totalCredit) {

		String journalId = "MONTHLY";
		String txType = "N";

		System.out.println("Salaries");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void generateTaxTableXml(Document doc, Element masterFiles) throws IOException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "taxtable.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		List<TaxTableValues> taxTable = new ArrayList<TaxTableValues>();
		int count = 0;
		for (CSVRecord csvRecord : csvParser) {
			String rate = (!csvRecord.get("c2g__Rate__c").isEmpty()) ? csvRecord.get("c2g__Rate__c") : "Desconhecido";
			String taxCodeId = (!csvRecord.get("c2g__TaxCode__r.Id").isEmpty()) ? csvRecord.get("c2g__TaxCode__r.Id")
					: "Desconhecido";
			String taxCodeDescription = (!csvRecord.get("c2g__TaxCode__r.c2g__Description__c").isEmpty())
					? csvRecord.get("c2g__TaxCode__r.c2g__Description__c")
					: "Desconhecido";
			String taxCodeCountry = (!csvRecord.get("c2g__TaxCode__r.Country__c").isEmpty())
					? csvRecord.get("c2g__TaxCode__r.Country__c")
					: "Desconhecido";

			TaxTableValues values = new TaxTableValues(rate, taxCodeId, taxCodeDescription, taxCodeCountry);
			taxTable.add(values);
		}
		generateXmlFileTaxTable(doc, masterFiles, taxTable);
		// storeXmlFile(doc, "suppliers.xml");
	}

	private static void generateXmlFileTaxTable(Document doc, Element masterFiles, List<TaxTableValues> taxTable) {
		Map<String, String> taxCodeToTaxType = getTaxCodeToTaxTypeMap();
		Map<String, String> taxCodeToTaxCode = getTaxCodeToTaxCodeMap();
		Map<String, String> TaxCodeToCountryRegion = getTaxCodeToCountryRegion();

		Element TaxTable = doc.createElement("TaxTable");
		masterFiles.appendChild(TaxTable);

		for (TaxTableValues values : taxTable) {
			Element TaxTableEntry = doc.createElement("TaxTableEntry");
			TaxTable.appendChild(TaxTableEntry);

			Element TaxType = doc.createElement("TaxType");
			TaxType.appendChild(doc.createTextNode(taxCodeToTaxType.get(values.taxCodeId)));
			TaxTableEntry.appendChild(TaxType);

			Element TaxCountryRegion = doc.createElement("TaxCountryRegion");
			TaxCountryRegion.appendChild(doc.createTextNode(TaxCodeToCountryRegion.get(values.taxCodeId)));
			TaxTableEntry.appendChild(TaxCountryRegion);

			Element TaxCode = doc.createElement("TaxCode");
			TaxCode.appendChild(doc.createTextNode(taxCodeToTaxCode.get(values.taxCodeId)));
			TaxTableEntry.appendChild(TaxCode);

			Element Description = doc.createElement("Description");
			Description.appendChild(doc.createTextNode(values.taxCodeDescription));
			TaxTableEntry.appendChild(Description);

			Element TaxPercentage = doc.createElement("TaxPercentage");
			TaxPercentage.appendChild(doc.createTextNode(values.rate));
			TaxTableEntry.appendChild(TaxPercentage);
		}
	}

	private static Map<String, String> getTaxCodeToTaxCodeMap() {
		Map<String, String> taxCodeToTaxCode = new HashMap<String, String>();

		taxCodeToTaxCode.put("a3e240000004zgAAAQ", "NOR");
		taxCodeToTaxCode.put("a3e240000004zgFAAQ", "NOR");
		taxCodeToTaxCode.put("a3e240000004zgKAAQ", "NOR");
		taxCodeToTaxCode.put("a3e240000004zg5AAA", "NOR");
		taxCodeToTaxCode.put("a3e24000000ZMdTAAW", "ISE");

		taxCodeToTaxCode.put("a3e24000000ZMddAAG", "ISE");
		taxCodeToTaxCode.put("a3e24000000ZMdYAAW", "ISE");
		taxCodeToTaxCode.put("a3e24000000d6mPAAQ", "ISE");
		taxCodeToTaxCode.put("a3e24000000d6myAAA", "NOR");
		taxCodeToTaxCode.put("a3e24000000d6ncAAA", "NOR");

		taxCodeToTaxCode.put("a3e24000000d6nhAAA", "ISE");
		taxCodeToTaxCode.put("a3e24000000d6qvAAA", "NOR");
		taxCodeToTaxCode.put("a3e24000000d6qqAAA", "NOR");
		taxCodeToTaxCode.put("a3e24000000d6r0AAA", "NOR");
		taxCodeToTaxCode.put("a3e24000000d6rAAAQ", "ISE");

		taxCodeToTaxCode.put("a3e24000000d6qbAAA", "NOR");
		taxCodeToTaxCode.put("a3e24000000d6qHAAQ", "NOR");
		taxCodeToTaxCode.put("a3e24000000d6qlAAA", "NOR");
		taxCodeToTaxCode.put("a3e24000000d6qWAAQ", "ISE");
		taxCodeToTaxCode.put("a3e24000000d6sIAAQ", "ISE");

		taxCodeToTaxCode.put("a3e240000005YBCAA2", "RED");
		taxCodeToTaxCode.put("a3e24000000HWMtAAO", "NOR");
		taxCodeToTaxCode.put("a3e24000000HWMyAAO", "NOR");
		taxCodeToTaxCode.put("a3e24000000ZfK6AAK", "INT");
		taxCodeToTaxCode.put("a3e1o000000kYLcAAM", "NOR");

		taxCodeToTaxCode.put("a3e1o000000kYLXAA2", "ISE");
		taxCodeToTaxCode.put("a3e1o000000kYLhAAM", "NOR");
		taxCodeToTaxCode.put("a3e1o000000kYLmAAM", "NOR");

		return taxCodeToTaxCode;
	}

	private static Map<String, String> getTaxCodeToCountryRegion() {
		Map<String, String> taxCodeToTaxCode = new HashMap<String, String>();

		taxCodeToTaxCode.put("a3e240000004zgAAAQ", "PT-AC");
		taxCodeToTaxCode.put("a3e240000004zgFAAQ", "PT-AC");
		taxCodeToTaxCode.put("a3e240000004zgKAAQ", "PT-MA");
		taxCodeToTaxCode.put("a3e240000004zg5AAA", "PT-AC");
		taxCodeToTaxCode.put("a3e24000000ZMdTAAW", "PT");

		taxCodeToTaxCode.put("a3e24000000ZMddAAG", "PT-MA");
		taxCodeToTaxCode.put("a3e24000000ZMdYAAW", "PT-AC");
		taxCodeToTaxCode.put("a3e24000000d6mPAAQ", "PT");
		taxCodeToTaxCode.put("a3e24000000d6myAAA", "PT");
		taxCodeToTaxCode.put("a3e24000000d6ncAAA", "PT");

		taxCodeToTaxCode.put("a3e24000000d6nhAAA", "PT");
		taxCodeToTaxCode.put("a3e24000000d6qvAAA", "PT");
		taxCodeToTaxCode.put("a3e24000000d6qqAAA", "PT");
		taxCodeToTaxCode.put("a3e24000000d6r0AAA", "PT");
		taxCodeToTaxCode.put("a3e24000000d6rAAAQ", "PT");

		taxCodeToTaxCode.put("a3e24000000d6qbAAA", "PT-AC");
		taxCodeToTaxCode.put("a3e24000000d6qHAAQ", "PT");
		taxCodeToTaxCode.put("a3e24000000d6qlAAA", "PT-MA");
		taxCodeToTaxCode.put("a3e24000000d6qWAAQ", "PT");
		taxCodeToTaxCode.put("a3e24000000d6sIAAQ", "PT");

		taxCodeToTaxCode.put("a3e240000005YBCAA2", "PT");
		taxCodeToTaxCode.put("a3e24000000HWMtAAO", "PT");
		taxCodeToTaxCode.put("a3e24000000HWMyAAO", "PT");
		taxCodeToTaxCode.put("a3e24000000ZfK6AAK", "PT");
		taxCodeToTaxCode.put("a3e1o000000kYLcAAM", "PT");

		taxCodeToTaxCode.put("a3e1o000000kYLXAA2", "PT");
		taxCodeToTaxCode.put("a3e1o000000kYLhAAM", "PT");
		taxCodeToTaxCode.put("a3e1o000000kYLmAAM", "PT");

		return taxCodeToTaxCode;
	}

	private static Map<String, String> getTaxCodeToTaxTypeMap() {
		Map<String, String> taxCodeToTaxType = new HashMap<String, String>();

		taxCodeToTaxType.put("a3e240000004zgAAAQ", "IVA");
		taxCodeToTaxType.put("a3e240000004zgFAAQ", "IVA");
		taxCodeToTaxType.put("a3e240000004zgKAAQ", "IVA");
		taxCodeToTaxType.put("a3e240000004zg5AAA", "IVA");
		taxCodeToTaxType.put("a3e24000000ZMdTAAW", "NS");

		taxCodeToTaxType.put("a3e24000000ZMddAAG", "NS");
		taxCodeToTaxType.put("a3e24000000ZMdYAAW", "NS");
		taxCodeToTaxType.put("a3e24000000d6mPAAQ", "NS");
		taxCodeToTaxType.put("a3e24000000d6myAAA", "IVA");
		taxCodeToTaxType.put("a3e24000000d6ncAAA", "IVA");

		taxCodeToTaxType.put("a3e24000000d6nhAAA", "NS");
		taxCodeToTaxType.put("a3e24000000d6qvAAA", "IVA");
		taxCodeToTaxType.put("a3e24000000d6qqAAA", "IVA");
		taxCodeToTaxType.put("a3e24000000d6r0AAA", "IVA");
		taxCodeToTaxType.put("a3e24000000d6rAAAQ", "NS");

		taxCodeToTaxType.put("a3e24000000d6qbAAA", "IVA");
		taxCodeToTaxType.put("a3e24000000d6qHAAQ", "IVA");
		taxCodeToTaxType.put("a3e24000000d6qlAAA", "IVA");
		taxCodeToTaxType.put("a3e24000000d6qWAAQ", "NS");
		taxCodeToTaxType.put("a3e24000000d6sIAAQ", "NS");

		taxCodeToTaxType.put("a3e240000005YBCAA2", "IVA");
		taxCodeToTaxType.put("a3e24000000HWMtAAO", "IVA");
		taxCodeToTaxType.put("a3e24000000HWMyAAO", "IVA");
		taxCodeToTaxType.put("a3e24000000ZfK6AAK", "IVA");
		taxCodeToTaxType.put("a3e1o000000kYLcAAM", "IVA");

		taxCodeToTaxType.put("a3e1o000000kYLXAA2", "NS");// ???
		taxCodeToTaxType.put("a3e1o000000kYLhAAM", "IVA");
		taxCodeToTaxType.put("a3e1o000000kYLmAAM", "IVA");

		return taxCodeToTaxType;
	}

	private static void generateProductsXml(Document doc, Element masterFiles) throws IOException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "products.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Set<ProductValues> products = new HashSet<ProductValues>();
		int count = 0;
		for (CSVRecord csvRecord : csvParser) {
			String id = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String productCode = (!csvRecord.get("ProductCode").isEmpty()) ? csvRecord.get("ProductCode")
					: "Desconhecido";
			String name = (!csvRecord.get("Name").isEmpty()) ? csvRecord.get("Name") : "Desconhecido";

			ProductValues values = new ProductValues(id, name, productCode);
			products.add(values);
		}
		generateXmlFileProducts(doc, masterFiles, products);
		// storeXmlFile(doc, "suppliers.xml");
	}

	private static void generateXmlFileProducts(Document doc, Element masterFiles, Set<ProductValues> products) {
		Map<String, String> productCodeToProductType = getProductCodeToProductTypeMap();

		for (ProductValues values : products) {
			Element Product = doc.createElement("Product");
			masterFiles.appendChild(Product);

			Element ProductType = doc.createElement("ProductType");
			ProductType.appendChild(doc.createTextNode(productCodeToProductType.get(values.productCode)));
			Product.appendChild(ProductType);

			Element ProductCode = doc.createElement("ProductCode");
			ProductCode.appendChild(doc.createTextNode(values.productCode));
			Product.appendChild(ProductCode);

			Element ProductDescription = doc.createElement("ProductDescription");
			ProductDescription.appendChild(doc.createTextNode(values.name));
			Product.appendChild(ProductDescription);

			Element ProductNumberCode = doc.createElement("ProductNumberCode");
			ProductNumberCode.appendChild(doc.createTextNode(values.productCode));
			Product.appendChild(ProductNumberCode);
		}
	}

	private static Map<String, String> getProductCodeToProductTypeMap() {
		Map<String, String> productCodeToProductType = new HashMap<String, String>();
		// S - serviços
		// O - outros
		// I - impostos, normalmente relacionados com o estado
		// P - produto, algo palpavel

		// Rendas
		productCodeToProductType.put("PT01", "S");
		productCodeToProductType.put("PT08", "S");
		productCodeToProductType.put("PT09", "S");
		productCodeToProductType.put("PT10", "S");
		productCodeToProductType.put("PT18", "S");
		productCodeToProductType.put("PT19", "S");
		// Venda equipamento usado
		productCodeToProductType.put("PT20", "S");
		// Seguros
		productCodeToProductType.put("PT02", "S");
		productCodeToProductType.put("PT11", "S");
		// Outros
		productCodeToProductType.put("PT03", "O");
		productCodeToProductType.put("PT04", "O");
		productCodeToProductType.put("PT05", "O");
		productCodeToProductType.put("PT06", "O");
		productCodeToProductType.put("PT07", "O");
		productCodeToProductType.put("PT12", "O");
		productCodeToProductType.put("PT13", "O");
		productCodeToProductType.put("PT14", "O");
		productCodeToProductType.put("PT15", "O");
		productCodeToProductType.put("PT16", "O");
		productCodeToProductType.put("PT17", "O");
		productCodeToProductType.put("PT22", "O");
		productCodeToProductType.put("PT23", "O");
		productCodeToProductType.put("PT24", "O");
		productCodeToProductType.put("PT25", "O");

		return productCodeToProductType;
	}

	private static void generateGlasXml(Document doc, Element masterFiles) throws IOException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "glas.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		List<GlaValues> glas = new ArrayList<GlaValues>();
		int count = 0;
		for (CSVRecord csvRecord : csvParser) {
			// Accessing values by Header names
			String id = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String name = (!csvRecord.get("Name").isEmpty()) ? csvRecord.get("Name") : "Desconhecido";
			String type = (!csvRecord.get("c2g__Type__c").isEmpty()) ? csvRecord.get("c2g__Type__c") : "Desconhecido";
			String trial1 = (!csvRecord.get("c2g__TrialBalance1__c").isEmpty()) ? csvRecord.get("c2g__TrialBalance1__c")
					: null;
			String trial2 = (!csvRecord.get("c2g__TrialBalance2__c").isEmpty()) ? csvRecord.get("c2g__TrialBalance2__c")
					: null;
			String trial3 = (!csvRecord.get("c2g__TrialBalance3__c").isEmpty()) ? csvRecord.get("c2g__TrialBalance3__c")
					: null;
			String trial4 = (!csvRecord.get("c2g__TrialBalance4__c").isEmpty()) ? csvRecord.get("c2g__TrialBalance4__c")
					: null;
			String code = (!csvRecord.get("c2g__ReportingCode__c").isEmpty()) ? csvRecord.get("c2g__ReportingCode__c")
					: "Desconhecido";

			GlaValues values = new GlaValues(id, name, type, trial1, trial2, trial3, trial4, code);
			glas.add(values);
		}
		generateXmlFileGla(doc, masterFiles, glas);
		// storeXmlFile(doc, "suppliers.xml");
	}

	private static void generateXmlFileGla(Document doc, Element masterFiles, List<GlaValues> glas) {
		Map<String, List<String>> prefixToGlas = getPrefixToGlas(glas);

		Element GeneralLedgerAccounts = doc.createElement("GeneralLedgerAccounts");
		masterFiles.appendChild(GeneralLedgerAccounts);

		Element TaxonomyReference = doc.createElement("TaxonomyReference");
		TaxonomyReference.appendChild(doc.createTextNode("S"));
		GeneralLedgerAccounts.appendChild(TaxonomyReference);

		for (GlaValues values : glas) {
			Element Account = doc.createElement("Account");
			GeneralLedgerAccounts.appendChild(Account);

			Element AccountID = doc.createElement("AccountID");
			AccountID.appendChild(doc.createTextNode(values.reportingCode));
			Account.appendChild(AccountID);

			Element AccountDescription = doc.createElement("AccountDescription");
			AccountDescription.appendChild(doc.createTextNode(values.name));
			Account.appendChild(AccountDescription);

			Element OpeningDebitBalance = doc.createElement("OpeningDebitBalance");
			OpeningDebitBalance.appendChild(doc.createTextNode("" + 0000));
			Account.appendChild(OpeningDebitBalance);

			Element OpeningCreditBalance = doc.createElement("OpeningCreditBalance");
			OpeningCreditBalance.appendChild(doc.createTextNode("" + 0000));
			Account.appendChild(OpeningCreditBalance);

			Element ClosingDebitBalance = doc.createElement("ClosingDebitBalance");
			ClosingDebitBalance.appendChild(doc.createTextNode("" + 0000));
			Account.appendChild(ClosingDebitBalance);

			Element ClosingCreditBalance = doc.createElement("ClosingCreditBalance");
			ClosingCreditBalance.appendChild(doc.createTextNode("" + 0000));
			Account.appendChild(ClosingCreditBalance);

			String groupingCategory = getGroupingCategory(values, prefixToGlas);
			Element GroupingCategory = doc.createElement("GroupingCategory");
			GroupingCategory.appendChild(doc.createTextNode(groupingCategory));
			Account.appendChild(GroupingCategory);

			String groupingCode = getGroupingCode(values);
			if(groupingCode != "") {
				Element GroupingCode = doc.createElement("GroupingCode");
				GroupingCode.appendChild(doc.createTextNode(groupingCode));
				Account.appendChild(GroupingCode);
			}

			if (groupingCategory == "GM") {
				Element TaxonomyCode = doc.createElement("TaxonomyCode");
				TaxonomyCode.appendChild(doc.createTextNode("1"));
				Account.appendChild(TaxonomyCode);
			}
		}
	}

	private static String getGroupingCode(GlaValues values) {
		String superiorAccount = (values.trial4 != null) ? values.trial4.split("-")[0].trim()
				: (values.trial3 != null) ? values.trial3.split("-")[0].trim()
						: (values.trial2 != null) ? values.trial2.split("-")[0].trim()
								: (values.trial1 != null) ? values.trial1.split("-")[0].trim() : "";

		return superiorAccount;
	}

	private static String getGroupingCategory(GlaValues values, Map<String, List<String>> prefixToGlas) {
		String glaShortName = values.reportingCode;
		if (glaShortName.length() == 1) {
			return "GR";
		}

		List<String> glas = prefixToGlas.get(glaShortName);
		String groupingCategory = (glas.size() == 1) ? "GM" : "GA";
		return groupingCategory;
	}

	private static Map<String, List<String>> getPrefixToGlas(List<GlaValues> glas) {
		Map<String, List<String>> prefixToGlas = new HashMap<String, List<String>>();
		for (GlaValues values : glas) {
			prefixToGlas.put(values.reportingCode, new ArrayList<>());
		}

		for (GlaValues values : glas) {
			String code = values.reportingCode;
			Integer i;
			for (i = 0; i < code.length(); i++) {
				String prefix = code.substring(0, Math.min(code.length(), i + 1));
				if (prefixToGlas.containsKey(prefix)) {
					List<String> aux = prefixToGlas.get(prefix);
					aux.add(code);
					prefixToGlas.put(prefix, aux);
				}
			}
		}

		return prefixToGlas;
	}

	private static void generatePcrXml(Document doc, Element generalLedgerEntries) throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "pcrs.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();
		// "Id","NIF__c","Name","BillingStreet","BillingCity","BillingPostalCode","BillingCountryCode"
		for (CSVRecord csvRecord : csvParser) {
			// Accessing values by Header names
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFilePcr(doc, generalLedgerEntries, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "pcrs.xml");
	}

	private static void generateXmlFilePcr(Document doc, Element generalLedgerEntries,
			Map<String, TxValues> txToDetails, Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries,
			BigDecimal totalDebit, BigDecimal totalCredit) {

		String journalId = "PCR";
		String journalDescription = "Notas de Crédito a Fornecedores";
		String txType = "N";

		Element journal = doc.createElement("Journal");
		generalLedgerEntries.appendChild(journal);

		Element JournalID = doc.createElement("JournalID");
		JournalID.appendChild(doc.createTextNode(journalId));
		journal.appendChild(JournalID);

		Element jDescription = doc.createElement("Description");
		jDescription.appendChild(doc.createTextNode(journalDescription));
		journal.appendChild(jDescription);

		System.out.println("PCR");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void generatePinXml(Document doc, Element GeneralLedgerEntries) throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "pins.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();
		// "Id","NIF__c","Name","BillingStreet","BillingCity","BillingPostalCode","BillingCountryCode"
		for (CSVRecord csvRecord : csvParser) {
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFilePin(doc, GeneralLedgerEntries, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "pins.xml");
	}

	private static void generateXmlFilePin(Document doc, Element GeneralLedgerEntries,
			Map<String, TxValues> txToDetails, Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries,
			BigDecimal totalDebit, BigDecimal totalCredit) {

		String journalId = "PIN";
		String journalDescription = "Pagamentos a Fornecedores";
		String txType = "N";

		Element journal = doc.createElement("Journal");
		GeneralLedgerEntries.appendChild(journal);

		Element JournalID = doc.createElement("JournalID");
		JournalID.appendChild(doc.createTextNode(journalId));
		journal.appendChild(JournalID);

		Element jDescription = doc.createElement("Description");
		jDescription.appendChild(doc.createTextNode(journalDescription));
		journal.appendChild(jDescription);

		System.out.println("PIN");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void generateCorrectionsXml(Document doc, Element generalLedgerEntries)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException,
			IOException {
		Set<String> invoiceIds = getRentInvoiceIds();

		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "corrections.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();

		for (CSVRecord csvRecord : csvParser) {
			String invoiceId = csvRecord.get("c2g__Transaction__r.c2g__SalesInvoice__c");
			if (invoiceIds.contains(invoiceId))
				continue;// DONT consider rents + insurances

			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFileCorrections(doc, generalLedgerEntries, txToDetails, txToLineItems, numEntries, totalDebit,
				totalCredit);
		// storeXmlFile(doc, "div.xml");
	}

	private static void generateXmlFileCorrections(Document doc, Element journal, Map<String, TxValues> txToDetails,
			Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries, BigDecimal totalDebit,
			BigDecimal totalCredit) {

		String journalId = "DIV";
		String txType = "J";

		System.out.println("DIV");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);
	}

	private static void generateRentsXml(Document doc, Element generalLedgerEntries)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException,
			IOException {
		Set<String> invoiceIds = getRentInvoiceIds();

		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "rents.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();

		for (CSVRecord csvRecord : csvParser) {
			String invoiceId = csvRecord.get("c2g__Transaction__r.c2g__SalesInvoice__c");
			if (!invoiceIds.contains(invoiceId))
				continue;// only consider rents + insurances (aka in invoiceIds)

			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFileRents(doc, generalLedgerEntries, txToDetails, txToLineItems, numEntries, totalDebit,
				totalCredit);
		// storeXmlFile(doc, "rents.xml");
	}

	private static Set<String> getRentInvoiceIds() throws IOException {
		Reader auxReader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "invoices.csv"));
		CSVParser auxCsvParser = new CSVParser(auxReader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Set<String> invoiceIds = new HashSet<>();
		for (CSVRecord auxCsvRecord : auxCsvParser) {
			String invoiceId = auxCsvRecord.get("c2g__Invoice__c");
			invoiceIds.add(invoiceId);
		}
		return invoiceIds;
	}

	private static void generateCshXml(Document doc, Element generalLedgerEntries)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException,
			IOException {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "cshs.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		Integer numEntries = 0;
		BigDecimal totalDebit = new BigDecimal(0);
		BigDecimal totalCredit = new BigDecimal(0);

		Map<String, TxValues> txToDetails = new HashMap<String, TxValues>();
		Map<String, List<TxLineItemValues>> txToLineItems = new HashMap<String, List<TxLineItemValues>>();
		// "Id","NIF__c","Name","BillingStreet","BillingCity","BillingPostalCode","BillingCountryCode"
		for (CSVRecord csvRecord : csvParser) {
			String txId = parseTransactionValues(txToDetails, csvRecord);

			// line items
			String liId = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String liTotalDebit = (!csvRecord.get("TLDebit__c").isEmpty()) ? csvRecord.get("TLDebit__c")
					: "Desconhecido";
			String liTotalCredit = (!csvRecord.get("TLCredit__c").isEmpty()) ? csvRecord.get("TLCredit__c")
					: "Desconhecido";
			String liGla = (!csvRecord.get("c2g__GeneralLedgerAccount__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__c")
					: "Desconhecido";
			String liCreatedDate = (!csvRecord.get("CreatedDate").isEmpty()) ? csvRecord.get("CreatedDate")
					: "Desconhecido";
			String liDescription = (!csvRecord.get("c2g__LineDescription__c").isEmpty())
					? csvRecord.get("c2g__LineDescription__c")
					: "Desconhecido";
			String liGlaCode = (!csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c").isEmpty())
					? csvRecord.get("c2g__GeneralLedgerAccount__r.c2g__ReportingCode__c")
					: "Desconhecido";

			TxLineItemValues liValues = new TxLineItemValues(liId, liTotalDebit, liTotalCredit, liGla, liCreatedDate,
					liDescription, liGlaCode);
			List<TxLineItemValues> aux = (txToLineItems.containsKey(txId)) ? txToLineItems.get(txId)
					: new ArrayList<TxLineItemValues>();
			aux.add(liValues);
			txToLineItems.put(txId, aux);

			numEntries++;
			totalDebit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalDebit)));
			totalCredit = totalDebit.add(BigDecimal.valueOf(Double.parseDouble(liTotalCredit)));
		}
		_numEntries = _numEntries.add(BigInteger.valueOf(numEntries));
		_totalDebit = _totalDebit.add(totalDebit);
		_totalCredit = _totalCredit.add(totalCredit);
		generateXmlFileCsh(doc, generalLedgerEntries, txToDetails, txToLineItems, numEntries, totalDebit, totalCredit);
		// storeXmlFile(doc, "csh.xml");
	}

	private static String parseTransactionValues(Map<String, TxValues> txToDetails, CSVRecord csvRecord) {
		String txId = (!csvRecord.get("c2g__Transaction__r.Id").isEmpty()) ? csvRecord.get("c2g__Transaction__r.Id")
				: "Desconhecido";
		String txCreatedDate = (!csvRecord.get("c2g__Transaction__r.c2g__TransactionDate__c").isEmpty())
				? csvRecord.get("c2g__Transaction__r.c2g__TransactionDate__c")
				: "Desconhecido";
		String txCreatedBy = (!csvRecord.get("c2g__Transaction__r.CreatedBy.Name").isEmpty())
				? csvRecord.get("c2g__Transaction__r.CreatedBy.Name")
				: "Desconhecido";
		String txDescription = (!csvRecord.get("c2g__Transaction__r.c2g__DocumentDescription__c").isEmpty())
				? csvRecord.get("c2g__Transaction__r.c2g__DocumentDescription__c")
				: "Desconhecido";
		String txDocNumber = (!csvRecord.get("c2g__Transaction__r.c2g__DocumentNumber__c").isEmpty())
				? csvRecord.get("c2g__Transaction__r.c2g__DocumentNumber__c")
				: "Desconhecido";
		String txAccountId = (!csvRecord.get("c2g__Transaction__r.c2g__Account__c").isEmpty())
				? csvRecord.get("c2g__Transaction__r.c2g__Account__c")
				: "Desconhecido";
		String txAccountType = (!csvRecord.get("c2g__Transaction__r.c2g__Account__r.Type").isEmpty())
				? csvRecord.get("c2g__Transaction__r.c2g__Account__r.Type")
				: "Desconhecido";
		String txPeriod = (!csvRecord.get("c2g__Transaction__r.c2g__Period__r.c2g__PeriodNumber__c").isEmpty())
				? csvRecord.get("c2g__Transaction__r.c2g__Period__r.c2g__PeriodNumber__c")
				: "Desconhecido";

		TxValues txValues = new TxValues(txId, txCreatedDate, txCreatedBy, txDescription, txDocNumber, txAccountId,
				txAccountType, txPeriod);
		txToDetails.put(txId, txValues);
		return txId;
	}

	private static void generateXmlFileRents(Document doc, Element journal, Map<String, TxValues> txToDetails,
			Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries, BigDecimal totalDebit,
			BigDecimal totalCredit) {
		String journalId = "MONTHLY";
		String txType = "N";

		System.out.println("RENTS");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);

	}

	private static void generateXmlFileCsh(Document doc, Element generalLedgerEntries,
			Map<String, TxValues> txToDetails, Map<String, List<TxLineItemValues>> txToLineItems, Integer numEntries,
			BigDecimal totalDebit, BigDecimal totalCredit) {
		String journalId = "CSH";
		String journalDescription = "Cash Entries";
		String txType = "N";

		Element journal = doc.createElement("Journal");
		generalLedgerEntries.appendChild(journal);

		Element JournalID = doc.createElement("JournalID");
		JournalID.appendChild(doc.createTextNode(journalId));
		journal.appendChild(JournalID);

		Element jDescription = doc.createElement("Description");
		jDescription.appendChild(doc.createTextNode(journalDescription));
		journal.appendChild(jDescription);

		System.out.println("CSH");
		populateTransactionSection(doc, txToDetails, txToLineItems, journalId, txType, journal, numEntries, totalDebit,
				totalCredit);

	}

	private static void populateTransactionSection(Document doc, Map<String, TxValues> txToDetails,
			Map<String, List<TxLineItemValues>> txToLineItems, String journalId, String txType, Element journal,
			Integer numEntries, BigDecimal totalDebit, BigDecimal totalCredit) {

		System.out.println("numEntries " + numEntries);
		System.out.println("tDebit " + totalDebit);
		System.out.println("tCredit " + totalCredit);

		for (String txId : txToDetails.keySet()) {
			Element Transaction = doc.createElement("Transaction");
			journal.appendChild(Transaction);

			TxValues txValues = txToDetails.get(txId);
			String txDate = txValues.createdDate.split("T")[0];
			String saftTxID = txDate + " " + journalId + " " + txValues.docNumber;

			Element TransactionID = doc.createElement("TransactionID");
			TransactionID.appendChild(doc.createTextNode(saftTxID));
			Transaction.appendChild(TransactionID);

			Element Period = doc.createElement("Period");
			Period.appendChild(doc.createTextNode(txValues.period));
			Transaction.appendChild(Period);

			Element TransactionDate = doc.createElement("TransactionDate");
			TransactionDate.appendChild(doc.createTextNode(txDate));
			Transaction.appendChild(TransactionDate);

			Element SourceID = doc.createElement("SourceID");
			SourceID.appendChild(doc.createTextNode(txValues.createdBy));
			Transaction.appendChild(SourceID);

			Element txDescription = doc.createElement("Description");
			txDescription.appendChild(doc.createTextNode(txValues.description));
			Transaction.appendChild(txDescription);

			Element DocArchivalNumber = doc.createElement("DocArchivalNumber");
			DocArchivalNumber.appendChild(doc.createTextNode(txValues.docNumber));
			Transaction.appendChild(DocArchivalNumber);

			Element TransactionType = doc.createElement("TransactionType");
			TransactionType.appendChild(doc.createTextNode(txType));
			Transaction.appendChild(TransactionType);

			Element GLPostingDate = doc.createElement("GLPostingDate");
			GLPostingDate.appendChild(doc.createTextNode(txDate));
			Transaction.appendChild(GLPostingDate);

			List<TxLineItemValues> lineItems = txToLineItems.get(txValues.id);
			if (lineItems.size() > 0) {
				Element Lines = doc.createElement("Lines");
				Transaction.appendChild(Lines);

				for (TxLineItemValues liValues : lineItems) {
					Double tDebit = Double.parseDouble(liValues.tDebit);
					Double tCredit = Double.parseDouble(liValues.tCredit);
					if (tDebit == 0 && tCredit == 0)
						continue;

					String typeOfLine = "";
					String amountType = "";
					Double amount = 0.0;

					if (tDebit > 0) {
						typeOfLine = "DebitLine";
						amountType = "DebitAmount";
						amount = tDebit;
					} else if (tCredit > 0) {
						typeOfLine = "CreditLine";
						amountType = "CreditAmount";
						amount = tCredit;
					}

					Element Line = doc.createElement(typeOfLine);
					Lines.appendChild(Line);

					Element RecordID = doc.createElement("RecordID");
					RecordID.appendChild(doc.createTextNode(liValues.id));
					Line.appendChild(RecordID);

					Element AccountID = doc.createElement("AccountID");
					AccountID.appendChild(doc.createTextNode(liValues.glaCode));
					Line.appendChild(AccountID);

					Element SystemEntryDate = doc.createElement("SystemEntryDate");
					SystemEntryDate.appendChild(doc.createTextNode(liValues.createdDate));
					Line.appendChild(SystemEntryDate);

					Element liDescription = doc.createElement("Description");
					liDescription.appendChild(doc.createTextNode(liValues.description));
					Line.appendChild(liDescription);

					Element amountValue = doc.createElement(amountType);
					amountValue.appendChild(doc.createTextNode("" + amount));
					Line.appendChild(amountValue);
				}
			}

		}
	}

	private static void generateCustomersXml(Document doc, Element masterFiles) throws IOException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "customers.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		int count = 0;
		List<CustomerValues> customerValues = new ArrayList<CustomerValues>();
		// "Id","NIF__c","Name","BillingStreet","BillingCity","BillingPostalCode","BillingCountryCode"
		for (CSVRecord csvRecord : csvParser) {
			// Accessing values by Header names
			String id = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String nif = (!csvRecord.get("NIF__c").isEmpty()) ? csvRecord.get("NIF__c") : "Desconhecido";
			String name = (!csvRecord.get("Name").isEmpty()) ? csvRecord.get("Name") : "Desconhecido";
			String street = (!csvRecord.get("BillingStreet").isEmpty()) ? csvRecord.get("BillingStreet")
					: "Desconhecido";
			String city = (!csvRecord.get("BillingCity").isEmpty()) ? csvRecord.get("BillingCity") : "Desconhecido";
			String postalCode = (!csvRecord.get("BillingPostalCode").isEmpty()) ? csvRecord.get("BillingPostalCode")
					: "Desconhecido";
			String country = (!csvRecord.get("BillingCountryCode").isEmpty()) ? csvRecord.get("BillingCountryCode")
					: "Desconhecido";

			CustomerValues values = new CustomerValues(id, nif, name, street, city, postalCode, country);
			customerValues.add(values);
		}
		generateXmlFileCustomer(doc, masterFiles, customerValues);
		// storeXmlFile(doc, "customers.xml");
	}

	private static void generateSuppliersXml(Document doc, Element masterFiles) throws IOException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		Reader reader = Files.newBufferedReader(Paths.get(CSV_DIRECTORY_PATH + "suppliers.csv"));
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		List<SupplierValues> supplierValues = new ArrayList<SupplierValues>();
		int count = 0;
		// "Id","NIF__c","Name","BillingStreet","BillingCity","BillingPostalCode","BillingCountryCode"
		for (CSVRecord csvRecord : csvParser) {
			// Accessing values by Header names
			String id = (!csvRecord.get("Id").isEmpty()) ? csvRecord.get("Id") : "Desconhecido";
			String nif = (!csvRecord.get("NIF__c").isEmpty()) ? csvRecord.get("NIF__c") : "Desconhecido";
			String name = (!csvRecord.get("Name").isEmpty()) ? csvRecord.get("Name") : "Desconhecido";
			String street = (!csvRecord.get("BillingStreet").isEmpty()) ? csvRecord.get("BillingStreet")
					: "Desconhecido";
			String city = (!csvRecord.get("BillingCity").isEmpty()) ? csvRecord.get("BillingCity") : "Desconhecido";
			String postalCode = (!csvRecord.get("BillingPostalCode").isEmpty()) ? csvRecord.get("BillingPostalCode")
					: "Desconhecido";
			String country = (!csvRecord.get("BillingCountryCode").isEmpty()) ? csvRecord.get("BillingCountryCode")
					: "Desconhecido";

			SupplierValues values = new SupplierValues(id, nif, name, street, city, postalCode, country);
			supplierValues.add(values);
		}
		generateXmlFileSupplier(doc, masterFiles, supplierValues);
		// storeXmlFile(doc, "suppliers.xml");
	}

	private static void generateXmlFileSupplier(Document doc, Element masterFiles,
			List<SupplierValues> supplierValues) {
		for (SupplierValues values : supplierValues) {
			Element supplier = doc.createElement("Supplier");
			masterFiles.appendChild(supplier);

			Element id = doc.createElement("SupplierID");
			id.appendChild(doc.createTextNode(values.id));
			supplier.appendChild(id);

			Element accountID = doc.createElement("AccountID");
			accountID.appendChild(doc.createTextNode(values.accountId));
			supplier.appendChild(accountID);

			Element nif = doc.createElement("SupplierTaxID");
			nif.appendChild(doc.createTextNode(values.nif));
			supplier.appendChild(nif);

			Element name = doc.createElement("CompanyName");
			name.appendChild(doc.createTextNode(values.name));
			supplier.appendChild(name);

			Element billingAddress = doc.createElement("BillingAddress");
			supplier.appendChild(billingAddress);

			Element AddressDetail = doc.createElement("AddressDetail");
			AddressDetail.appendChild(doc.createTextNode(values.street));
			billingAddress.appendChild(AddressDetail);

			Element City = doc.createElement("City");
			City.appendChild(doc.createTextNode(values.city));
			billingAddress.appendChild(City);

			Element PostalCode = doc.createElement("PostalCode");
			PostalCode.appendChild(doc.createTextNode(values.postalCode));
			billingAddress.appendChild(PostalCode);

			Element Country = doc.createElement("Country");
			Country.appendChild(doc.createTextNode(values.country));
			billingAddress.appendChild(Country);

			Element SelfBillingIndicator = doc.createElement("SelfBillingIndicator");
			SelfBillingIndicator.appendChild(doc.createTextNode("" + 0));
			supplier.appendChild(SelfBillingIndicator);
		}
	}

	private static void generateXmlFileCustomer(Document doc, Element masterFiles,
			List<CustomerValues> customerValues) {
		for (CustomerValues values : customerValues) {
			Element supplier = doc.createElement("Customer");
			masterFiles.appendChild(supplier);

			Element id = doc.createElement("CustomerID");
			id.appendChild(doc.createTextNode(values.id));
			supplier.appendChild(id);

			Element accountID = doc.createElement("AccountID");
			accountID.appendChild(doc.createTextNode(values.accountId));
			supplier.appendChild(accountID);

			Element nif = doc.createElement("CustomerTaxID");
			nif.appendChild(doc.createTextNode(values.nif));
			supplier.appendChild(nif);

			Element name = doc.createElement("CompanyName");
			name.appendChild(doc.createTextNode(values.name));
			supplier.appendChild(name);

			Element billingAddress = doc.createElement("BillingAddress");
			supplier.appendChild(billingAddress);

			Element AddressDetail = doc.createElement("AddressDetail");
			AddressDetail.appendChild(doc.createTextNode(values.street));
			billingAddress.appendChild(AddressDetail);

			Element City = doc.createElement("City");
			City.appendChild(doc.createTextNode(values.city));
			billingAddress.appendChild(City);

			Element PostalCode = doc.createElement("PostalCode");
			PostalCode.appendChild(doc.createTextNode(values.postalCode));
			billingAddress.appendChild(PostalCode);

			Element Country = doc.createElement("Country");
			Country.appendChild(doc.createTextNode(values.country));
			billingAddress.appendChild(Country);

			Element SelfBillingIndicator = doc.createElement("SelfBillingIndicator");
			SelfBillingIndicator.appendChild(doc.createTextNode("" + 0));
			supplier.appendChild(SelfBillingIndicator);
		}
	}

	private static void storeXmlFile(Document doc, String fileName)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(TEST_DIRECTORY_PATH + fileName));
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
		transformer.transform(source, result);
		System.out.println("File saved!");
	}
}
