import { Button, Modal } from "react-bootstrap";
import { useTranslation } from "react-i18next";

const ModalPage = (props: {
  buttonName: string;
  show: boolean;
  onClose: any;
  onConfirm: any;
}) => {
  const { t } = useTranslation();

  return (
    <>
      <Modal show={props.show} onHide={props.onClose}>
        <Modal.Header closeButton>
          <Modal.Title>{t("Warning")}</Modal.Title>
        </Modal.Header>

        <Modal.Body>{t("dashboard.ConfirmationGeneric")}</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={props.onClose}>
            {t("cancel")}
          </Button>
          <Button variant="primary" onClick={props.onConfirm}>
            {props.buttonName}
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default ModalPage;
