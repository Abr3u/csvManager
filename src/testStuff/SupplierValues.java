package testStuff;

public class SupplierValues{
	public String id;
	public String nif;
	public String name;
	public String street;
	public String city;
	public String postalCode;
	public String country;
	public final String accountId = "22114";
	
	public SupplierValues(String id, String nif, String name, String street, String city, String postalCode, String country) {
		this.id = id;
		this.name = name;
		this.nif = nif;
		this.street = street;
		this.city = city;
		this.postalCode = postalCode;
		this.country = country;
	}
}