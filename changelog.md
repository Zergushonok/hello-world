# Compared to v1
## Limitations of the naive matcher
The initial implementation cannot process more than a few dozens of orders in a reasonable time because:
- It has to check every order in the list
- For every order, it has to, again, iterate over every order in the list until it finds a match
- It stores processed orders in a separate list and checks every new order against this list to skip the orders that have already been matched. This means that, as the number of matched orders grows, more and more time is needed to check, whether a new order has already been matched or not.

As a result, the time complexity looks like O(n^2) when we have no matching orders at all, and will probably be worse in a more typical case, when the processed orders list increases in size as we go down the initial list of orders.

## Ways to optimize
- We can represent the orders not as a single list, but as a two maps -- for two types of orders (buy and sell)
- The asset (tradaeble good) will be used as a key, and the list of orders for this asset -- as its value
- This way, when looking for a match, we'll be able to look only among the counterpart orders for the same asset, thus dramatically reducing the number of candidates
- And since the maximum number of possible matches is the size of a lesser map (if there are 3 selling orders but 10 buying orders, we cannot have more than 3 matches), the algorithm should iterate over the map with less entries. Which means that if there are no selling orders at all, for example, the algorithm will return immediately
- We can probably remove the matched orders from the maps instead of adding them to a separate buffer

This is not the final algorithm but rather only a possible approach that may be subject to change. 