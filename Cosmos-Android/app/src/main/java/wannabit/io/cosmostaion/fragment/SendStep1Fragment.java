package wannabit.io.cosmostaion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.SendActivity;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.dialog.Dialog_Empty_Warnning;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.utils.WDp;

import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_BNB;
import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_IRIS;
import static wannabit.io.cosmostaion.base.BaseConstant.IS_TEST;


public class SendStep1Fragment extends BaseFragment implements View.OnClickListener {

    private Button              mBefore, mNextBtn;
    private EditText            mAmountInput;
    private TextView            mAvailableAmount;
    private TextView            mDenomTitle;
    private ImageView           mClearAll;
    private Button              mAdd01, mAdd1, mAdd10, mAdd100, mAddHalf, mAddMax;
    private BigDecimal          mMaxAvailable = BigDecimal.ZERO;

    private ArrayList<Coin>     mToSendCoins = new ArrayList<>();

    public static SendStep1Fragment newInstance(Bundle bundle) {
        SendStep1Fragment fragment = new SendStep1Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_step1, container, false);
        mBefore = rootView.findViewById(R.id.btn_before);
        mNextBtn = rootView.findViewById(R.id.btn_next);
        mAmountInput = rootView.findViewById(R.id.et_amount_coin);
        mAvailableAmount = rootView.findViewById(R.id.tv_max_coin);
        mDenomTitle = rootView.findViewById(R.id.tv_symbol_coin);
        mClearAll = rootView.findViewById(R.id.clearAll);
        mAdd01 = rootView.findViewById(R.id.btn_add_01);
        mAdd1 = rootView.findViewById(R.id.btn_add_1);
        mAdd10 = rootView.findViewById(R.id.btn_add_10);
        mAdd100 = rootView.findViewById(R.id.btn_add_100);
        mAddHalf = rootView.findViewById(R.id.btn_add_half);
        mAddMax = rootView.findViewById(R.id.btn_add_all);
        mBefore.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mClearAll.setOnClickListener(this);
        mAdd01.setOnClickListener(this);
        mAdd1.setOnClickListener(this);
        mAdd10.setOnClickListener(this);
        mAdd100.setOnClickListener(this);
        mAddHalf.setOnClickListener(this);
        mAddMax.setOnClickListener(this);

        mAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable et) {
                String es = et.toString().trim();
                if(TextUtils.isEmpty(es)) {
                    mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                } else if (es.startsWith(".")) {
                    mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                    mAmountInput.setText("");
                } else if (es.endsWith(".")) {
                    mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                    mAmountInput.setVisibility(View.VISIBLE);
                } else if(es.length() > 1 && es.startsWith("0") && !es.startsWith("0.")) {
                    mAmountInput.setText("0");
                    mAmountInput.setSelection(1);
                }

                if (getSActivity().mBaseChain.equals(BaseChain.COSMOS_MAIN)) {
                    if(es.equals("0.000000")) {
                        mAmountInput.setText("0.00000");
                        mAmountInput.setSelection(7);
                    } else {
                        try {
                            final BigDecimal inputAmount = new BigDecimal(es);
                            if (BigDecimal.ZERO.compareTo(inputAmount) >= 0 ){
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                                return;
                            }
                            BigDecimal checkPosition = inputAmount.movePointRight(6);
                            try {
                                Long.parseLong(checkPosition.toPlainString());
                            } catch (Exception e) {
                                String recover = es.substring(0, es.length() - 1);
                                mAmountInput.setText(recover);
                                mAmountInput.setSelection(recover.length());
                                return;
                            }
                            if(inputAmount.compareTo(mMaxAvailable.movePointLeft(6).setScale(6, RoundingMode.CEILING)) > 0) {
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                            } else {
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                            }
                            mAmountInput.setSelection(mAmountInput.getText().length());
                        } catch (Exception e) { }
                    }

                } else if (getSActivity().mBaseChain.equals(BaseChain.IRIS_MAIN)) {
                    if(es.equals("0.000000000000000000")) {
                        mAmountInput.setText("0.00000000000000000");
                        mAmountInput.setSelection(19);
                    } else {
                        try {
                            final BigDecimal inputAmount = new BigDecimal(es);
                            if (BigDecimal.ZERO.compareTo(inputAmount) >= 0 ){
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                                return;
                            }

                            BigDecimal checkPosition = inputAmount.movePointRight(18);
                            BigDecimal checkMax = checkPosition.setScale(0, RoundingMode.DOWN);
                            if (checkPosition.compareTo(checkMax) != 0) {
                                String recover = es.substring(0, es.length() - 1);
                                mAmountInput.setText(recover);
                                mAmountInput.setSelection(recover.length());
                                return;
                            }

                            if(inputAmount.compareTo(mMaxAvailable.movePointLeft(18).setScale(18, RoundingMode.CEILING)) > 0) {
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                            } else {
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                            }
                            mAmountInput.setSelection(mAmountInput.getText().length());
                        } catch (Exception e) { }
                    }

                } else if (getSActivity().mBaseChain.equals(BaseChain.BNB_MAIN)) {
                    if(es.equals("0.00000000")) {
                        mAmountInput.setText("0.0000000");
                        mAmountInput.setSelection(9);
                    } else {
                        try {
                            final BigDecimal inputAmount = new BigDecimal(es);
                            if (BigDecimal.ZERO.compareTo(inputAmount) >= 0 ){
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                                return;
                            }
                            BigDecimal checkPosition = inputAmount.movePointRight(8);
                            BigDecimal checkMax = checkPosition.setScale(0, RoundingMode.DOWN);
                            if (checkPosition.compareTo(checkMax) != 0) {
                                String recover = es.substring(0, es.length() - 1);
                                mAmountInput.setText(recover);
                                mAmountInput.setSelection(recover.length());
                                return;
                            }
                            if(inputAmount.compareTo(mMaxAvailable) > 0) {
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                            } else {
                                mAmountInput.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                            }
                            mAmountInput.setSelection(mAmountInput.getText().length());
                        } catch (Exception e) { }
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isAdded() || getSActivity() == null || getSActivity().mAccount == null) {
            getSActivity().onBackPressed();
        }

        if (getSActivity().mBaseChain.equals(BaseChain.COSMOS_MAIN)) {
            mMaxAvailable = getSActivity().mAccount.getAtomBalance().subtract(BigDecimal.ONE);
            mAvailableAmount.setText(WDp.getDpAmount(getContext(), mMaxAvailable, 6, getSActivity().mBaseChain));

        } else if (getSActivity().mBaseChain.equals(BaseChain.IRIS_MAIN)) {
            mDenomTitle.setText(getSActivity().mIrisToken.base_token.symbol.toUpperCase());
            if (getSActivity().mIrisToken.base_token.equals(COSMOS_IRIS)) {
                mMaxAvailable = getSActivity().mAccount.getIrisBalance().subtract(new BigDecimal("200000000000000000"));
                mAvailableAmount.setText(WDp.getDpAmount(getContext(), mMaxAvailable, 18, getSActivity().mBaseChain));
                mDenomTitle.setTextColor(getResources().getColor(R.color.colorIris));
            } else {
                //TODO check with IRIS TOKEN

            }

        } else if (getSActivity().mBaseChain.equals(BaseChain.BNB_MAIN)) {
            mDenomTitle.setText(getSActivity().mBnbToken.original_symbol.toUpperCase());
            if (getSActivity().mBnbToken.symbol.equals(COSMOS_BNB)) {
                mMaxAvailable = getSActivity().mAccount.getBnbBalance().subtract(new BigDecimal("0.000375"));
                mAvailableAmount.setText(WDp.getDpAmount(getContext(), mMaxAvailable, 8, getSActivity().mBaseChain));
                mDenomTitle.setTextColor(getResources().getColor(R.color.colorBnb));
            } else {
                mMaxAvailable = getSActivity().mAccount.getTokenBalance(getSActivity().mBnbToken.symbol);
                mAvailableAmount.setText(WDp.getDpAmount(getContext(), mMaxAvailable, 8, getSActivity().mBaseChain));
            }

        }

    }

    @Override
    public void onClick(View v) {
        if(v.equals(mBefore)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mNextBtn)) {
            if(isValidateSendAmount()) {
                getSActivity().mTargetCoins = mToSendCoins;
                getSActivity().onNextStep();
            } else {
                Toast.makeText(getContext(), R.string.error_invalid_amount, Toast.LENGTH_SHORT).show();
            }
        } else if (v.equals(mAdd01)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if(es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("0.1")).toPlainString());

        } else if (v.equals(mAdd1)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if(es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("1")).toPlainString());

        } else if (v.equals(mAdd10)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if(es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("10")).toPlainString());

        } else if (v.equals(mAdd100)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if(es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("100")).toPlainString());

        } else if (v.equals(mAddHalf)) {
            if (getSActivity().mBaseChain.equals(BaseChain.COSMOS_MAIN)) {
                mAmountInput.setText(mMaxAvailable.divide(new BigDecimal("2000000"), 6, RoundingMode.DOWN).toPlainString());
            } else if (getSActivity().mBaseChain.equals(BaseChain.IRIS_MAIN)) {
                mAmountInput.setText(mMaxAvailable.divide(new BigDecimal("2000000000000000000"), 18, RoundingMode.DOWN).toPlainString());
            } else if (getSActivity().mBaseChain.equals(BaseChain.BNB_MAIN)) {
                mAmountInput.setText(mMaxAvailable.divide(new BigDecimal("2"), 8, RoundingMode.DOWN).toPlainString());
            }

        } else if (v.equals(mAddMax)) {
            if (getSActivity().mBaseChain.equals(BaseChain.COSMOS_MAIN)) {
                mAmountInput.setText(mMaxAvailable.divide(new BigDecimal("1000000"), 6, RoundingMode.DOWN).toPlainString());
                onShowWarnDialog();
            } else if (getSActivity().mBaseChain.equals(BaseChain.IRIS_MAIN)) {
                mAmountInput.setText(mMaxAvailable.divide(new BigDecimal("1000000000000000000"), 18, RoundingMode.DOWN).toPlainString());
                if (getSActivity().mIrisToken.base_token.equals(COSMOS_IRIS)) {
                    onShowWarnDialog();
                }
            } else if (getSActivity().mBaseChain.equals(BaseChain.BNB_MAIN)) {
                mAmountInput.setText(mMaxAvailable.toPlainString());
                if (getSActivity().mBnbToken.symbol.equals(COSMOS_BNB)) {
                    onShowWarnDialog();
                }
            }


        } else if (v.equals(mClearAll)) {
            mAmountInput.setText("");

        }

    }

    private boolean isValidateSendAmount() {
        mToSendCoins.clear();
        try {
            if (getSActivity().mBaseChain.equals(BaseChain.COSMOS_MAIN)) {
                BigDecimal sendTemp = new BigDecimal(mAmountInput.getText().toString().trim());
                if (sendTemp.compareTo(BigDecimal.ZERO) <= 0) return false;
                if (sendTemp.compareTo(mMaxAvailable.movePointLeft(6).setScale(6, RoundingMode.CEILING)) > 0) return false;
                if (IS_TEST) {
                    Coin muon = new Coin(BaseConstant.COSMOS_MUON, sendTemp.multiply(new BigDecimal("1000000")).setScale(0).toPlainString());
                    mToSendCoins.add(muon);
                } else {
                    Coin atom = new Coin(BaseConstant.COSMOS_ATOM, sendTemp.multiply(new BigDecimal("1000000")).setScale(0).toPlainString());
                    mToSendCoins.add(atom);
                }
                return true;

            } else if (getSActivity().mBaseChain.equals(BaseChain.IRIS_MAIN)) {
                BigDecimal sendTemp = new BigDecimal(mAmountInput.getText().toString().trim());
                if (sendTemp.compareTo(BigDecimal.ZERO) <= 0) return false;
                if (sendTemp.compareTo(mMaxAvailable.movePointLeft(18).setScale(18, RoundingMode.CEILING)) > 0) return false;
                Coin iris = new Coin(BaseConstant.COSMOS_IRIS_ATTO, sendTemp.multiply(new BigDecimal("1000000000000000000")).setScale(0).toPlainString());
                mToSendCoins.add(iris);
                return true;

            } else if (getSActivity().mBaseChain.equals(BaseChain.BNB_MAIN)) {
                BigDecimal sendTemp = new BigDecimal(mAmountInput.getText().toString().trim());
                if (sendTemp.compareTo(BigDecimal.ZERO) <= 0) return false;
                if (sendTemp.compareTo(mMaxAvailable) > 0) return false;
                Coin token = new Coin(getSActivity().mBnbToken.symbol, sendTemp.toPlainString());
                mToSendCoins.add(token);
                return true;
            }
            return false;

        } catch (Exception e) {
            mToSendCoins.clear();
            return false;
        }
    }

    private void onShowWarnDialog() {
        Dialog_Empty_Warnning dialog = Dialog_Empty_Warnning.newInstance();
        dialog.setCancelable(true);
        dialog.show(getFragmentManager().beginTransaction(), "dialog");
    }

    private SendActivity getSActivity() {
        return (SendActivity)getBaseActivity();
    }


}
