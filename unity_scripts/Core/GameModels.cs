using System;
using System.Collections.Generic;
using UnityEngine;

namespace XienToiDe.Core
{
    public enum LocationType
    {
        CongTruongCap3,     // Học sinh đông, giá rẻ, kiên nhẫn thấp, tuần tra vừa
        KhuKtxDaiHoc,       // Sinh viên, ưa combo & món mới, hoạt động tối
        PhoDiBoPhoCo        // Du khách, giá bán cao, tip khủng, đô thị gắt gao
    }

    public enum CustomerType
    {
        HocSinh,
        SinhVien,
        DuKhach,
        VangLai,
        Shipper             // Không tốn ghế, không xả rác nhưng đông quá sẽ tăng Risk
    }

    public enum FoodType
    {
        CaVienChien,
        XucXichRan,
        NemChuaRan,
        LapXuongNuongDa,    // Nướng sỏi/đá
        KhoaiLocXoay,
        TraChanh            // Đồ uống rót nhanh
    }

    public enum CookingStation
    {
        BepChien,
        BepNuongDa,
        ThungDoUong
    }

    public enum CookingState
    {
        Empty,
        Cooking,
        Done,
        Burnt
    }

    public enum SauceType
    {
        None,
        TuongOt,
        SotMe,
        TuongCa
    }

    public enum SpeakerLevel
    {
        Off,
        Low,
        High
    }

    [Serializable]
    public class FoodConfig
    {
        public FoodType foodType;
        public string displayName;
        public int price;
        public int cost;
        public CookingStation station;
        public float cookTimeSec = 3.5f;
        public float burnTimeSec = 6.5f;
        public bool isTrending = false;
    }

    [Serializable]
    public class CustomerOrder
    {
        public string orderId;
        public CustomerType customerType;
        public List<FoodType> requestedItems = new List<FoodType>();
        public SauceType requiredSauce;
        public float maxPatience = 20f;
        public float currentPatience = 20f;
        public bool isShipper = false;
        public string tableName;
    }

    [Serializable]
    public class PlayerProfileData
    {
        public int day = 1;
        public int level = 1;
        public int exp = 0;
        public int cash = 120000; // Tiền vốn khởi đầu (VNĐ)
        public int totalSkewersSold = 0;
        public int totalRevenue = 0;
        public int timesEscaped = 0;
        public int timesCaught = 0;
    }
}
