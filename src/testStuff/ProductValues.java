package testStuff;

public class ProductValues {
	public String id;
	public String name;
	public String productCode;

	public ProductValues(String id, String name, String productCode) {
		this.id = id;
		this.name = name;
		this.productCode = productCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProductValues) {
			ProductValues prod = (ProductValues) obj;
			if (this.productCode.equals(prod.productCode))
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.productCode.hashCode();
	}
}
