using UnityEngine;
using System;
using XienToiDe.Core;

namespace XienToiDe.Police
{
    public class RiskMeterManager : MonoBehaviour
    {
        public static RiskMeterManager Instance { get; private set; }

        [Range(0f, 100f)] public float currentRisk = 0f;

        // Các nguyên nhân tăng Risk
        public int deployedTables = 2; // Số bàn ghế bày lấn chiếm
        public SpeakerLevel speaker = SpeakerLevel.Low;
        public float trashAccumulation = 0f; // 0 đến 100
        public int waitingShippers = 0;
        public float basePatrolMultiplier = 1.0f; // Tùy địa điểm

        // Sự kiện Công an tới
        public bool isPoliceRaidActive = false;
        public float raidCountdown = 10f;
        public float tablesPackProgress = 0f; // 0 đến 1
        public float kitchenPackProgress = 0f; // 0 đến 1

        public event Action OnRiskCriticalWarning;
        public event Action OnPoliceRaidTriggered;
        public event Action<bool> OnPoliceRaidFinished; // true = dọn kịp, false = bị phạt

        private void Awake() => Instance = this;

        private void Update()
        {
            if (isPoliceRaidActive)
            {
                raidCountdown -= Time.deltaTime;
                if (raidCountdown <= 0f)
                {
                    // Hết 10 giây mà chưa dọn xong -> BỊ PHẠT TIỀN
                    isPoliceRaidActive = false;
                    OnPoliceRaidFinished?.Invoke(false);
                }
                return;
            }

            // Tính toán Risk theo GDD
            float tableRisk = deployedTables * 6.5f;
            float speakerRisk = speaker == SpeakerLevel.High ? 25f : (speaker == SpeakerLevel.Low ? 10f : 0f);
            float trashRisk = trashAccumulation * 0.35f;
            float shipperRisk = waitingShippers * 8f;

            currentRisk = Mathf.Clamp((tableRisk + speakerRisk + trashRisk + shipperRisk) * basePatrolMultiplier * 0.45f, 0f, 100f);

            // Tích rác dần theo thời gian nếu không quét
            trashAccumulation = Mathf.Clamp(trashAccumulation + Time.deltaTime * 0.8f, 0f, 100f);

            if (currentRisk >= 80f)
            {
                OnRiskCriticalWarning?.Invoke();
            }

            if (currentRisk >= 100f)
            {
                TriggerPoliceRaid();
            }
        }

        public void CleanTrash()
        {
            trashAccumulation = 0f;
        }

        private void TriggerPoliceRaid()
        {
            isPoliceRaidActive = true;
            raidCountdown = 10f;
            tablesPackProgress = 0f;
            kitchenPackProgress = 0f;
            OnPoliceRaidTriggered?.Invoke();
        }

        public void TapPackTables()
        {
            tablesPackProgress = Mathf.Clamp01(tablesPackProgress + 0.35f);
            CheckIfReadyToFlee();
        }

        public void TapPackKitchen()
        {
            kitchenPackProgress = Mathf.Clamp01(kitchenPackProgress + 0.35f);
            CheckIfReadyToFlee();
        }

        private void CheckIfReadyToFlee()
        {
            if (tablesPackProgress >= 1f && kitchenPackProgress >= 1f)
            {
                // Dọn kịp trước khi hết giờ -> Kích hoạt Mini-game lái xe!
                isPoliceRaidActive = false;
                OnPoliceRaidFinished?.Invoke(true);
            }
        }
    }
}
