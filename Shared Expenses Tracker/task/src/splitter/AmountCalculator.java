package splitter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AmountCalculator {

    private final BigDecimal minimumAmount;
    private final BigDecimal totalPrice;
    private final Set<String> temporary;
    private final String payerPerson;

    public AmountCalculator(BigDecimal totalPrice, Set<String> temporary, String payerPerson) {
        this.minimumAmount = new BigDecimal("0.01");
        this.totalPrice = totalPrice;
        this.temporary = new LinkedHashSet<>(temporary);
        this.payerPerson = payerPerson;
    }

    public BigDecimal calcSharedAmount(BigDecimal quantityPerson) {
        return totalPrice.divide(quantityPerson, RoundingMode.FLOOR);
    }

    public boolean hasRemainder(BigDecimal sharedAmount, BigDecimal quantityPerson) {
        return !totalPrice.equals(sharedAmount.multiply(quantityPerson)); //true
    }

    public Deque<String> calcExtraPayers(boolean haveRemainder, BigDecimal sharedAmount, BigDecimal quantityPerson) {
        BigDecimal remainderAmount = haveRemainder ? totalPrice.subtract(sharedAmount.multiply(quantityPerson)) : BigDecimal.ZERO; //0.02
        int extraPayersCount = haveRemainder ? remainderAmount.divide(minimumAmount, RoundingMode.DOWN).intValue() : 0; // 2

        List<String> temporaryList = new ArrayList<>(temporary);
        temporaryList.remove(payerPerson);

        return IntStream.range(0, extraPayersCount)
                .mapToObj(temporaryList::get)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public BigDecimal getMinimumAmount() {
        return minimumAmount;
    }
}
