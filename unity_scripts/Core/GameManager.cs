using UnityEngine;
using XienToiDe.Police;
using XienToiDe.MiniGame;
using XienToiDe.Cooking;
using XienToiDe.Customers;

namespace XienToiDe.Core
{
    public enum GameState
    {
        MainMenu,
        SetupShift,
        Gameplay,
        PoliceRaid,
        GetawayMiniGame,
        SummaryReport,
        UpgradeShop
    }

    public class GameManager : MonoBehaviour
    {
        public static GameManager Instance { get; private set; }

        public GameState currentState = GameState.MainMenu;
        public PlayerProfileData profile = new PlayerProfileData();
        public FoodType currentTrendingFood = FoodType.LapXuongNuongDa;
        public LocationType selectedLocation = LocationType.CongTruongCap3;

        private void Awake()
        {
            if (Instance == null) Instance = this;
            else Destroy(gameObject);
            DontDestroyOnLoad(gameObject);
        }

        public void StartShift(int tables, SpeakerLevel speaker, LocationType location)
        {
            selectedLocation = location;
            currentState = GameState.Gameplay;

            if (RiskMeterManager.Instance != null)
            {
                RiskMeterManager.Instance.deployedTables = tables;
                RiskMeterManager.Instance.speaker = speaker;
                RiskMeterManager.Instance.basePatrolMultiplier = location == LocationType.PhoDiBoPhoCo ? 1.75f :
                    (location == LocationType.KhuKtxDaiHoc ? 1.35f : 1.0f);
            }
        }

        public void OnPoliceEscapeSuccess()
        {
            currentState = GameState.GetawayMiniGame;
        }

        public void FinishDayShift(int revenue, int tips, int ingredientCost, int fines, int damages)
        {
            int netProfit = revenue + tips - ingredientCost - fines - damages;
            profile.cash = Mathf.Max(0, profile.cash + netProfit);
            profile.totalRevenue += (revenue + tips);
            profile.day++;
            currentState = GameState.SummaryReport;
        }
    }
}
