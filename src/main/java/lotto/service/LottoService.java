package lotto.service;

import camp.nextstep.edu.missionutils.Randoms;
import lotto.domain.Constants;
import lotto.domain.Lotto;
import lotto.domain.Ranking;

import java.util.*;
import java.util.stream.Collectors;

public class LottoService {
    public ArrayList<Lotto> getLottos(Integer price) {
        int lottoCount = price / Constants.PRICE_UNIT;
        ArrayList<Lotto> lottos = new ArrayList<>();

        for (int i = 0; i < lottoCount; i++) {
            lottos.add(issueLotto());
        }

        return lottos;
    }

    private Lotto issueLotto() {
        List<Integer> numbers = Randoms.pickUniqueNumbersInRange(Constants.MIN_LOTTO_NUM, Constants.MAX_LOTTO_NUM, Constants.LOTTO_LENGTH);

        return new Lotto(numbers);
    }

    public String getLottoList(ArrayList<Lotto> lottos) {
        return lottos.stream()
                .map(Lotto::toString)
                .collect(Collectors.joining("\n"));
    }

    public HashMap<Ranking, Integer> getWinningStatic(ArrayList<Lotto> lottos, Lotto winningNumber, Integer bonusNumber) {
        HashMap<Ranking, Integer> winningStatic = new HashMap<>();

        for (Lotto lotto: lottos) {
            Ranking rank = getLottoResult(lotto, winningNumber, bonusNumber);
            winningStatic.put(rank, winningStatic.getOrDefault(rank, 0) + 1);
        }

        return winningStatic;
    }

    private Ranking getLottoResult(Lotto lotto, Lotto winningNumber, Integer bonusNumber) {
        int matchCount = 0;
        boolean matchBonus = false;

        for (Integer number : lotto.getNumbers()) {
            if (winningNumber.getNumbers().contains(number)) {
                matchCount += 1;
            }

            if (number.equals(bonusNumber)) {
                matchBonus = true;
            }
        }

        return Ranking.getRanking(matchCount, matchBonus);
    }

    public String makeWinningStaticResult(HashMap<Ranking, Integer> winningStatic) {
        StringBuilder result = new StringBuilder();

        for (Ranking ranking : Ranking.values()) {
            if (ranking.getRank() == 6) continue;;

            result.append(ranking);
            result.append(String.format("- %d개", winningStatic.getOrDefault(ranking, 0)));
            result.append("\n");
        }

        return result.toString();
    }

    public Double getRateOfReturn(Integer price, HashMap<Ranking, Integer> winningStatic) {
        int profit = 0;

        for (Ranking ranking : Ranking.values()) {
            profit += calculateProfit(ranking, winningStatic.getOrDefault(ranking, 0));
        }

        return profit / (double) price * 100;
    }

    private Integer calculateProfit(Ranking ranking, Integer rankingCount) {
        return Integer.parseInt(ranking.getPrize()) * rankingCount;
    }
}
