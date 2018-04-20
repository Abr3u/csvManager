package testStuff;

public class TxLineItemValues {
	
	public String id;
	public String tDebit;
	public String tCredit;
	public String gla;
	public String createdDate;
	public String description;
	public String glaCode;

	public TxLineItemValues(String id, String tDebit, String tCredit, String gla, String createdDate, String description, String glaCode) {
		this.id = id;
		this.tDebit = tDebit;
		this.tCredit = tCredit;
		this.gla = gla;
		this.createdDate = createdDate;
		this.description = description;
		this.glaCode = glaCode;
	}
}
