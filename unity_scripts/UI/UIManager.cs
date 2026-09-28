using UnityEngine;
using UnityEngine.UI;
using XienToiDe.Core;
using XienToiDe.Police;
using XienToiDe.Cooking;
using XienToiDe.Customers;

namespace XienToiDe.UI
{
    public class UIManager : MonoBehaviour
    {
        public static UIManager Instance { get; private set; }

        [Header("Panels")]
        public GameObject mainMenuPanel;
        public GameObject setupShiftPanel;
        public GameObject gameplayPanel;
        public GameObject policeRaidPanel;
        public GameObject miniGamePanel;
        public GameObject summaryPanel;

        [Header("Gameplay HUD")]
        public Slider riskSlider;
        public Text cashText;
        public Text clockText;
        public Text raidCountdownText;
        public Slider tablesPackSlider;
        public Slider kitchenPackSlider;

        private void Awake() => Instance = this;

        private void Update()
        {
            if (GameManager.Instance == null) return;

            // Cập nhật HUD
            if (cashText != null)
            {
                cashText.text = $"{GameManager.Instance.profile.cash:N0} đ";
            }

            if (RiskMeterManager.Instance != null && riskSlider != null)
            {
                riskSlider.value = RiskMeterManager.Instance.currentRisk / 100f;
            }

            if (RiskMeterManager.Instance != null && RiskMeterManager.Instance.isPoliceRaidActive)
            {
                if (raidCountdownText != null)
                {
                    raidCountdownText.text = $"{RiskMeterManager.Instance.raidCountdown:F1}s";
                }
                if (tablesPackSlider != null)
                {
                    tablesPackSlider.value = RiskMeterManager.Instance.tablesPackProgress;
                }
                if (kitchenPackSlider != null)
                {
                    kitchenPackSlider.value = RiskMeterManager.Instance.kitchenPackProgress;
                }
            }
        }

        public void SwitchPanel(GameState state)
        {
            if (mainMenuPanel != null) mainMenuPanel.SetActive(state == GameState.MainMenu);
            if (setupShiftPanel != null) setupShiftPanel.SetActive(state == GameState.SetupShift);
            if (gameplayPanel != null) gameplayPanel.SetActive(state == GameState.Gameplay);
            if (policeRaidPanel != null) policeRaidPanel.SetActive(state == GameState.PoliceRaid);
            if (miniGamePanel != null) miniGamePanel.SetActive(state == GameState.GetawayMiniGame);
            if (summaryPanel != null) summaryPanel.SetActive(state == GameState.SummaryReport);
        }
    }
}
