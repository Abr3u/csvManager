package testStuff;

public class TaxTableValues {
	public String rate;
	public String taxCodeId;
	public String taxCodeDescription;
	public String taxCodeCountry;
	
	public TaxTableValues(String rate, String taxCodeId, String taxCodeDescription, String taxCodeCountry) {
		super();
		this.rate = rate;
		this.taxCodeId = taxCodeId;
		this.taxCodeDescription = taxCodeDescription;
		this.taxCodeCountry = taxCodeCountry;
	}
}
